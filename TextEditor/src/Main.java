import javax.swing.UIManager;

public class Main {
    public static void main(String args[]) throws Exception {
	    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
	    new TextEditor();
    }
}