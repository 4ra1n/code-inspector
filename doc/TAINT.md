## 通用污点传递规则

（1）数组`AASTORE`指令的处理

对数组某个位置赋值的指令一般如下

```text
ANEWARRAY java/lang/String
DUP
ICONST_0
ALOAD 1
AASTORE
```

如果设置到数组的新元素是污点，那么执行`AASTORE`指令后栈顶的`array ref`也应设为污点

（`AASTORE`指令弹出三个参数，此时栈顶被`DUP`的另一份`array ref`是保存污点元素的数组引用）

```java
@Override
public void visitInsn(int opcode) {
    if (opcode == Opcodes.AASTORE) {
        if (operandStack.get(0).contains(Taint.PARAM_TAINT) ||
                operandStack.get(0).contains(Taint.TO_STRING)) {
            super.visitInsn(opcode);
            operandStack.set(0, Taint.PARAM_TAINT);
            return;
        }
    }
    super.visitInsn(opcode);
}
```

（2）处理`getter`方法的传递

调用非静态方法且以`get`开头只有一个参数，当栈顶为污点时可以确定是`getter`方法，应该进行污点传递

```text
INVOKEVIRTUAL xxx/Obj.getXxx ()Ljava/lang/String;
```

```java
if (operandStack.size() > 0 &&
        operandStack.get(0).contains(Taint.PARAM_TAINT) &&
        opcode != Opcodes.INVOKESTATIC) {
    Type[] argTypes = Type.getArgumentTypes(desc);
    Type[] extendedArgTypes = new Type[argTypes.length + 1];
    System.arraycopy(argTypes, 0, extendedArgTypes, 1, argTypes.length);
    extendedArgTypes[0] = Type.getObjectType(owner);
    argTypes = extendedArgTypes;
    super.visitMethodInsn(opcode, owner, name, desc, itf);
    if (operandStack.size() > 0) {
        if (argTypes.length == 1 && name.startsWith("get")) {
            operandStack.set(0, Taint.PARAM_TAINT);
        }
    }
    return;
}
```

（3）一些常见的情况
当以下方法的参数是污点时，执行后认为返回值也被污染了，根据实际情况应该添加更多
- java/util/Base64$Decoder#decode
- java/lang/String#replace
- java/lang/String#format
- java/io/BufferedInputStream#<init>
- java/io/File#<init>
- java/io/FileInputStream#<init>

ref: https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html