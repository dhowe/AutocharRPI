package autochar;

import processing.core.PApplet;
import autochar.Sample.Type;

public class AutocharIndex extends PApplet {
	
	public static boolean PRODUCTION = false;
	public static boolean FULLSCREEN = false;
	public static String VERSION = "11";
	
	static final long serialVersionUID = -1541612L;

	static String OS = System.getProperty("os.name");
	static Sample bell, stroke;
	
    Autochar typer;
//    conf, word, tid, strokes = [];
//    var wmed = '';
//    var def = '';
//    var showDefs = 1;
//    var showMed = 1;
//    var doSound = 1;
//    var invertText = 0;
//    var eraseSpeed = 0;
//    var strokeDelay = 300;
//    var bgcol = [245, 245, 245];
//    var hitcol = [0, 0, 0];
//    var txtcol = [0, 0, 0];
//    var rgb = [0, 0, 0];

	// /////////////////////////////////////////////////

	public void setup() {
		noCursor();

		size(680, 490);

		System.out.println("[INFO] Autochar.version [" + VERSION + "]");

		bell = Sample.create(this, "bell.wav");
		//stroke = Sample.create(this, "stroke.wav");
		bell.play();
	}

	public void draw() {
		background(255);
	}

	public void mouseClicked() {
	}

	public void keyPressed() {
	}

	// undecorated frame
	public void init() {
		frame.removeNotify();
		frame.setUndecorated(true);
		frame.addNotify();
		super.init();
	}

	private static int parseIntArg(String arg, int tagLen) {
		try {
			return Integer.parseInt(arg.substring(tagLen).trim());
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println("Ignoring arg: " + arg);
			return -1;
		}
	}

	public static void parseOpts(String[] args) {
		if (OS.startsWith("Mac")) {
			Sample.type = Type.MINIM;
		}

		System.out.println("[OPTS] AudioLib=" + Sample.type);
	}

	public static void onError(Exception e) {
		System.err.println("[ERROR] " + e != null ? e.getMessage() : "unknown");
		if (!PRODUCTION)
			throw new RuntimeException(e);
	}

	public static void main(String[] args) {
		// System.out.println("Test Ok?"+getBestIpAddress().equals(getIpAddress("en0")));
		System.out.println("[INFO] " + System.getProperty("user.dir"));
		if (OS.startsWith("Mac"))
			args = new String[] { "-ng", "-np", "-nfs", "-nnet", "-i2",
					"-noip", "-cffffff" };

		if (args != null)
			parseOpts(args);

		args = FULLSCREEN ? new String[] { "--present", "--bgcolor=#ffffff",
				"--hide-stop", AutocharIndex.class.getName() }
				: new String[] { AutocharIndex.class.getName() };

		PApplet.main(args);
	}

}// end
