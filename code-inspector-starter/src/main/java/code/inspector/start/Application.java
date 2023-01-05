package code.inspector.start;

public class Application {
    public static void main(String[] args) {
        String logo = " ___   ___  ________  ________    _____  ________      \n" +
                "|\\  \\ |\\  \\|\\   __  \\|\\   __  \\  / __  \\|\\   ___  \\    \n" +
                "\\ \\  \\\\_\\  \\ \\  \\|\\  \\ \\  \\|\\  \\|\\/_|\\  \\ \\  \\\\ \\  \\   \n" +
                " \\ \\______  \\ \\   _  _\\ \\   __  \\|/ \\ \\  \\ \\  \\\\ \\  \\  \n" +
                "  \\|_____|\\  \\ \\  \\\\  \\\\ \\  \\ \\  \\   \\ \\  \\ \\  \\\\ \\  \\ \n" +
                "         \\ \\__\\ \\__\\\\ _\\\\ \\__\\ \\__\\   \\ \\__\\ \\__\\\\ \\__\\\n" +
                "          \\|__|\\|__|\\|__|\\|__|\\|__|    \\|__|\\|__| \\|__|\n";
        System.out.println(logo);
        System.out.println("Project: code-inspector");
        System.out.println("Version: 0.1-beta");
        System.out.println("Author : 4ra1n");
        System.out.println("Github : github.com/4ra1n/code-inspector");
        code.inspector.form.CodeInspector.start0();
    }
}
