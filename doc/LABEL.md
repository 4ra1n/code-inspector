## 如何处理分支

由于是分析字节码，不存在字节码解释器，所以遇到`if`和`for`循环等相关的跳转指令，实际上不会跳转，一直是顺序执行和分析。

以一个`SPEL`的`RCE`检测为例，我们从最简单的一个例子来看：
```java
@RequestMapping(path = "/")
public String index(String input) {
    ExpressionParser parser = new SpelExpressionParser();
    EvaluationContext evaluationContext = new StandardEvaluationContext();
    Expression expr = parser.parseExpression(input);
    expr.getValue(evaluationContext);
    return "ok";
}
```

这里有三个关键点：
- 必须调用`StandardEvaluationContext`类的初始化`<init>`方法
- `parseExpression`方法的参数必须是污点，进而传递污点到方法返回值`Expression`
- `getValue`方法的栈顶必须是`StandardEvaluationContext`类且栈顶下第二个必须是污点

换成检测代码如下：
```java
boolean spELStandard = spELOption && owner.equals(
                "org/springframework/expression/spel/support/StandardEvaluationContext") &&
                name.equals("<init>");
boolean spELParse = spELOption && owner.equals("org/springframework/expression/ExpressionParser") &&
        name.equals("parseExpression");
boolean spELGetValue = spELOption && owner.equals("org/springframework/expression/Expression") &&
        name.equals("getValue");

// 初始化StandardEvaluationContext类后
if (spELStandard) {
    super.visitMethodInsn(opcode, owner, name, desc, itf);
    operandStack.set(0, Taint.SPRING_STANDARD);
    return;
}
// parseExpression的参数（栈顶）如果是污点
// 执行该方法后返回值（栈顶）应该也设为污点
if (spELParse) {
    if (operandStack.get(0).contains(Taint.PARAM_TAINT)) {
        super.visitMethodInsn(opcode, owner, name, desc, itf);
        operandStack.set(0, Taint.PARAM_TAINT);
        return;
    }
}
// getValue是栈顶参数必须是StandardEvaluationContext
// 且栈顶下第二个元素必须是污点（在上一步已经传递了）
if (spELGetValue) {
    if (operandStack.get(0).contains(Taint.SPRING_STANDARD)) {
        if (operandStack.size() > 1 &&
                operandStack.get(1).contains(Taint.PARAM_TAINT)) {
            pass.put(Const.RCE_SP_EL_TYPE, true);
        }
        super.visitMethodInsn(opcode, owner, name, desc, itf);
        return;
    }
}
```

虽然以上代码我已经写了注释说明，不过换成字节码来看也许更容易理解：
```text
NEW org/springframework/expression/spel/support/StandardEvaluationContext
DUP
INVOKESPECIAL org/springframework/expression/spel/support/StandardEvaluationContext.<init> ()V
// 这里设置了StandardEvaluationContext专属污点
ASTORE 3

ALOAD 2
ALOAD 1
// 现在栈顶是用户输入的参数（污点）
INVOKEINTERFACE org/springframework/expression/ExpressionParser.parseExpression (Ljava/lang/String;)Lorg/springframework/expression/Expression; (itf)
// 返回值设置污点传递下去
ASTORE 4

ALOAD 4
ALOAD 3
// 此时栈中应该有两个元素
// 栈顶为context第二个为expression（this）
INVOKEINTERFACE org/springframework/expression/Expression.getValue (Lorg/springframework/expression/EvaluationContext;)Ljava/lang/Object; (itf)
```

以上的规则看起来似乎没有问题，但如果是这样的代码，将会无法检测

```java
@RequestMapping(path = "/")
public String index(String input) {
    ExpressionParser parser = new SpelExpressionParser();
    EvaluationContext evaluationContext = null;
    Expression expr = null;
    if (input.contains("test")) {
        evaluationContext = new StandardEvaluationContext();
    } else {
        evaluationContext = new SimpleEvaluationContext.Builder().build();
    }
    expr = parser.parseExpression(input);
    expr.getValue(evaluationContext);
    return "ok";
}
```

无法检测的原因可以从字节码分析出
```text
NEW org/springframework/expression/spel/support/StandardEvaluationContext
DUP
INVOKESPECIAL org/springframework/expression/spel/support/StandardEvaluationContext.<init> ()V
// 此时是成功设置污点的
ASTORE 3
// 无法处理跳转指令会继续分析
GOTO L6
L4
LINENUMBER 32 L4
// 初始化SimpleEvaluationContext
NEW org/springframework/expression/spel/support/SimpleEvaluationContext$Builder
DUP
ICONST_0
ANEWARRAY org/springframework/expression/PropertyAccessor
INVOKESPECIAL org/springframework/expression/spel/support/SimpleEvaluationContext$Builder.<init> ([Lorg/springframework/expression/PropertyAccessor;)V
INVOKEVIRTUAL org/springframework/expression/spel/support/SimpleEvaluationContext$Builder.build ()Lorg/springframework/expression/spel/support/SimpleEvaluationContext;
// 保存到相同的变量表位置导致覆盖
ASTORE 3
```

由于不会真正地处理跳转指令，所以会顺序分析到两个分支，导致污点信息被覆盖

当遇到跳转指令的时候，应该保存当前栈帧的污点信息，并且实际分析到跳转指令对应的`Label`时，恢复之前的栈帧污点信息到当前栈帧。

全局维护一个`Map`保存每个`Label`当前的栈帧污点信息

```java
private final Map<Label, GotoState<T>> gotoStates = new HashMap<>();

public class GotoState<T> {
    private LocalVariables<T> localVariables;
    private OperandStack<T> operandStack;
    // getter setter
}
```

当遇到跳转指令后的逻辑
```java
@Override
public void visitJumpInsn(int opcode, Label label) {
    switch (opcode) {
        case Opcodes.IFEQ:
        case Opcodes.IFNE:
        case Opcodes.IFLT:
        case Opcodes.IFGE:
        case Opcodes.IFGT:
        case Opcodes.IFLE:
        case Opcodes.IFNULL:
        case Opcodes.IFNONNULL:
            operandStack.pop();
            break;
        case Opcodes.IF_ICMPEQ:
        case Opcodes.IF_ICMPNE:
        case Opcodes.IF_ICMPLT:
        case Opcodes.IF_ICMPGE:
        case Opcodes.IF_ICMPGT:
        case Opcodes.IF_ICMPLE:
        case Opcodes.IF_ACMPEQ:
        case Opcodes.IF_ACMPNE:
            operandStack.pop();
            operandStack.pop();
            break;
        case Opcodes.GOTO:
            break;
        case Opcodes.JSR:
            operandStack.push();
            super.visitJumpInsn(opcode, label);
            return;
        default:
            throw new IllegalStateException("unsupported opcode: " + opcode);
    }
    // 和新方法
    mergeGotoState(label);
    super.visitJumpInsn(opcode, label);
    sanityCheck();
}
```

核心的保存于合并的方法`mergeGotoState`
```java
private void mergeGotoState(Label label) {
    // 如果之前存在了这个label将会合并当前的信息
    if (gotoStates.containsKey(label)) {
        GotoState<T> state = gotoStates.get(label);
        // old -> label
        LocalVariables<T> oldLocalVariables = state.getLocalVariables();
        OperandStack<T> oldOperandStack = state.getOperandStack();
        // new -> null
        LocalVariables<T> newLocalVariables = new LocalVariables<>();
        OperandStack<T> newOperandStack = new OperandStack<>();
        // init new
        for (Set<T> original : oldLocalVariables.getList()) {
            newLocalVariables.add(new HashSet<>(original));
        }
        for (Set<T> original : oldOperandStack.getList()) {
            newOperandStack.add(new HashSet<>(original));
        }
        // 合并当前栈帧的污点信息
        // 长度不足情况补位后再添加
        // add current state
        for (int i = 0; i < localVariables.size(); i++) {
            while (newLocalVariables.size()<=i){
                newLocalVariables.add(new HashSet<>());
            }
            newLocalVariables.get(i).addAll(localVariables.get(i));
        }
        for (int i = 0; i < operandStack.size(); i++) {
            while (newOperandStack.size()<=i){
                newOperandStack.add(new HashSet<>());
            }
            newOperandStack.get(i).addAll(operandStack.get(i));
        }
        // set new state
        GotoState<T> newGotoState = new GotoState<>();
        newGotoState.setOperandStack(newOperandStack);
        newGotoState.setLocalVariables(newLocalVariables);
        gotoStates.put(label, newGotoState);
    } else {
        // 如果之前没有保存过这个label当前的信息新建即可
        LocalVariables<T> oldLocalVariables = localVariables;
        OperandStack<T> oldOperandStack = operandStack;
        // new -> null
        LocalVariables<T> newLocalVariables = new LocalVariables<>();
        OperandStack<T> newOperandStack = new OperandStack<>();
        // init new
        for (Set<T> original : oldLocalVariables.getList()) {
            newLocalVariables.add(new HashSet<>(original));
        }
        for (Set<T> original : oldOperandStack.getList()) {
            newOperandStack.add(new HashSet<>(original));
        }
        // set new state
        GotoState<T> newGotoState = new GotoState<>();
        newGotoState.setOperandStack(newOperandStack);
        newGotoState.setLocalVariables(newLocalVariables);
        gotoStates.put(label, newGotoState);
    }
}
```

当`visit`到对应的`label`时代码如下

```java
@Override
public void visitLabel(Label label) {
    if (gotoStates.containsKey(label)) {
        GotoState<T> state = gotoStates.get(label);
        // old -> label
        LocalVariables<T> oldLocalVariables = state.getLocalVariables();
        OperandStack<T> oldOperandStack = state.getOperandStack();
        // new -> null
        LocalVariables<T> newLocalVariables = new LocalVariables<>();
        OperandStack<T> newOperandStack = new OperandStack<>();
        // init new
        for (Set<T> original : oldLocalVariables.getList()) {
            newLocalVariables.add(new HashSet<>(original));
        }
        for (Set<T> original : oldOperandStack.getList()) {
            newOperandStack.add(new HashSet<>(original));
        }
        // 设置回跳转指令执行之前的栈帧污点信息
        this.operandStack = newOperandStack;
        this.localVariables = newLocalVariables;
    }
    if (exceptionHandlerLabels.contains(label)) {
        operandStack.push(new HashSet<>());
    }
    super.visitLabel(label);
    sanityCheck();
}
```

通过以上的分支处理逻辑，案例中的漏洞将可以被检测到