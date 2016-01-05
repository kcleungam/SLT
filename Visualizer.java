
public class Visualizer {

	public static void main(String[] args) {
		try {  
            System.loadLibrary("jogl");  
            System.out.println("Yes");  
        } catch (Exception e) {  
            System.out.println("No:" + e.getMessage());  
        }  
	}

}
