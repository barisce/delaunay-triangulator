import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import utilities.DelaunayTriangulator;
import utilities.NotEnoughPointsException;
import utilities.Triangle2D;
import utilities.Vector2D;

public class DelaunayTriangulationDemo extends JFrame {

	private static final Dimension DIMENSION = new Dimension(1200, 600);
	private static final Color COLOR_TRIANGLE_FILL = new Color(26, 121, 121);
	private static final Color COLOR_TRIANGLE_EDGES = new Color(5, 234, 234);
	private static final Color COLOR_TRIANGLE_BORDER = new Color(241, 241, 121);
	private static final Color COLOR_BACKGROUND = new Color(47, 47, 47);
	private DelaunayTriangulator delaunayTriangulator;
	private DelaunayTriangulator trueTriangulation;
	private Timer timer;
	private long begin;
	private JPanel panel;
	private JTextField textField;
	private JLabel movesLabel;
	private JLabel timerLabel;
	private int numOfMoves = 0;
	private int numOfPoints = 25;
	private boolean initialized = false;
	private boolean won = false;
	private Random generator = new Random();

	private BasicStroke stroke = new BasicStroke(3);

	private int[] triangleXList;
	private int[] triangleYList;

	private DelaunayTriangulationDemo () {
		JFrame f = new JFrame("Delaunay Triangulation Example");
		f.setSize(1300, 800);
//		f.setLocation(300, 300);
		f.setResizable(false);
		f.setBackground(COLOR_BACKGROUND);

		triangleXList = new int[3];
		triangleYList = new int[3];

		panel = new JPanel() {
			{
				addMouseListener(new MouseAdapter() {
					public void mousePressed (MouseEvent e) {
						if (!won) {
							if (delaunayTriangulator.flipEdge(new Vector2D(e.getX(), e.getY()))) numOfMoves++;
							movesLabel.setText("Moves moved: " + numOfMoves);

							won = delaunayTriangulator.winCheck(delaunayTriangulator.getTriangles(), trueTriangulation.getTriangles());

							if(won) {
								movesLabel.setText("Moves moved: " + numOfMoves + " YOU WON " + timerLabel.getText());
								timer.stop();
							}

							repaint();
						} else {
							System.out.println("WON");
						}
					}

					public void mouseReleased (MouseEvent e) {
					}
				});
				addMouseMotionListener(new MouseMotionAdapter() {
					public void mouseMoved (MouseEvent e) {
					}

					public void mouseDragged (MouseEvent e) {
					}
				});
			}

			public void paint (Graphics g) {
				super.paint(g);

				if (initialized) {
					for (Triangle2D triangle2D : trueTriangulation.getTriangles()) {
						if (triangle2D != null) {
							triangleXList[0] = (int) triangle2D.a.x;
							triangleYList[0] = (int) triangle2D.a.y;
							triangleXList[1] = (int) triangle2D.b.x;
							triangleYList[1] = (int) triangle2D.b.y;
							triangleXList[2] = (int) triangle2D.c.x;
							triangleYList[2] = (int) triangle2D.c.y;

							Graphics2D g2 = (Graphics2D) g;
							g2.setStroke(stroke);
							g.setColor(COLOR_TRIANGLE_EDGES);
							g.drawLine(triangleXList[0], triangleYList[0], triangleXList[1], triangleYList[1]);
							g.drawLine(triangleXList[1], triangleYList[1], triangleXList[2], triangleYList[2]);
							g.drawLine(triangleXList[2], triangleYList[2], triangleXList[0], triangleYList[0]);
							g.setColor(COLOR_TRIANGLE_FILL);
							g.fillPolygon(triangleXList, triangleYList, 3);

							drawPoint(g, triangle2D.a.x, triangle2D.a.y, COLOR_TRIANGLE_BORDER);
							drawPoint(g, triangle2D.b.x, triangle2D.b.y, COLOR_TRIANGLE_BORDER);
							drawPoint(g, triangle2D.c.x, triangle2D.c.y, COLOR_TRIANGLE_BORDER);
						}
					}
					for (Triangle2D triangle2D : delaunayTriangulator.getTriangles()) {
						if (triangle2D != null) {
							triangleXList[0] = (int) triangle2D.a.x;
							triangleYList[0] = (int) triangle2D.a.y;
							triangleXList[1] = (int) triangle2D.b.x;
							triangleYList[1] = (int) triangle2D.b.y;
							triangleXList[2] = (int) triangle2D.c.x;
							triangleYList[2] = (int) triangle2D.c.y;

							Graphics2D g2 = (Graphics2D) g;
							g2.setStroke(stroke);
							g.setColor(COLOR_TRIANGLE_EDGES);
							g.drawLine(triangleXList[0], triangleYList[0], triangleXList[1], triangleYList[1]);
							g.drawLine(triangleXList[1], triangleYList[1], triangleXList[2], triangleYList[2]);
							g.drawLine(triangleXList[2], triangleYList[2], triangleXList[0], triangleYList[0]);
							g.setColor(COLOR_TRIANGLE_FILL);
							g.fillPolygon(triangleXList, triangleYList, 3);

							drawPoint(g, triangle2D.a.x, triangle2D.a.y, COLOR_TRIANGLE_BORDER);
							drawPoint(g, triangle2D.b.x, triangle2D.b.y, COLOR_TRIANGLE_BORDER);
							drawPoint(g, triangle2D.c.x, triangle2D.c.y, COLOR_TRIANGLE_BORDER);
						}
					}
				}
			}
		};
		panel.setBackground(COLOR_BACKGROUND);
		panel.setSize(new Dimension(1200, 700));
		f.add(panel);

		//init();
		setUI(f);
		f.setVisible(true);
	}

	public static void main (String[] args) {
		SwingUtilities.invokeLater(DelaunayTriangulationDemo::new);
	}

	private void setUI (JFrame f) {
		JPanel panel = new JPanel();
		JLabel label = new JLabel("Number of Points: ");
		label.setSize(new Dimension(40, 40));
		panel.add(label);
		textField = new JTextField(3);
		textField.setSize(new Dimension(40, 40));
		textField.setText("25");
		panel.add(textField);
		JButton setNumOfPoints = new JButton("Set Number of Points");
		setNumOfPoints.setSize(new Dimension(40, 40));
		panel.add(setNumOfPoints);
		movesLabel = new JLabel("Moves moved: 0");
		movesLabel.setSize(new Dimension(40, 40));
		panel.add(movesLabel);
		JButton finishButton = new JButton("Solve Automatically");
		finishButton.setSize(new Dimension(40, 40));
		panel.add(finishButton);
		timerLabel = new JLabel("Time Elapsed: 00:00");
		timerLabel.setSize(new Dimension(40, 40));
		panel.add(timerLabel);
		f.add(panel, BorderLayout.SOUTH);
		setNumOfPoints.addActionListener(new TextListener());
		finishButton.addActionListener(new ButtonListener());
	}

	private void init () {
		initialized = true;
		won = false;
		List<Vector2D> pointSet = new ArrayList<>();
		List<Vector2D> delaunayPointSet = new ArrayList<>();
		numOfMoves = 0;

		for (int i = 0; i < numOfPoints; i++) {
			pointSet.add(new Vector2D(generator.nextInt(DIMENSION.height - 50) + 50, generator.nextInt(DIMENSION.height - 50) + 50));
			delaunayPointSet.add(new Vector2D(pointSet.get(i).x + DIMENSION.height, pointSet.get(i).y));
		}

		delaunayTriangulator = new DelaunayTriangulator(pointSet);
		trueTriangulation = new DelaunayTriangulator(delaunayPointSet);

		try {
			delaunayTriangulator.triangulate();
			int j = 5 + generator.nextInt(6);
			for (int i = 0; i < j; i++) {
				delaunayTriangulator.flipEdge(new Vector2D(generator.nextInt(DIMENSION.height - 100) + 100, generator.nextInt(DIMENSION.height - 100) + 100));
			}
			trueTriangulation.triangulate();
		} catch (NotEnoughPointsException ignored) {

		}

		begin = System.currentTimeMillis();
	}

	private void drawPoint (Graphics g, double x, double y, Color color) {
		g.setColor(color);
		g.fillOval((int) x - 4, (int) y - 4, 8, 8);
		g.setColor(Color.WHITE);
		g.drawString("(" + (int) x + "," + (int) y + ")", (int) x + 5, (int) y + 5);
	}

	class TextListener implements ActionListener {
		public void actionPerformed (ActionEvent event) {
			String radiusText = textField.getText();
			numOfPoints = Integer.parseInt(radiusText);
			init();
			timer = new Timer(1000, e -> {
				SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.UK);
				timerLabel.setText("Time Elapsed: " + sdf.format(System.currentTimeMillis() - begin));
			});
			timer.start();
			movesLabel.setText("Moves moved: " + numOfMoves);
			panel.repaint();
			repaint();
		}
	}

	class ButtonListener implements ActionListener {
		public void actionPerformed (ActionEvent event) {
			try {
				delaunayTriangulator.triangulate();
				won = delaunayTriangulator.winCheck(delaunayTriangulator.getTriangles(), trueTriangulation.getTriangles());
				timer.stop();
				panel.repaint();
				repaint();
			} catch (NotEnoughPointsException ignored) {
			}
		}
	}
}
