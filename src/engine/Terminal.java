package engine;

import javafx.scene.control.Label;
import javafx.geometry.Pos;
import java.util.Random;
import javafx.application.Platform;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.text.TextFlow;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.FontWeight;

public class Terminal {
	static TextFlow flow = new TextFlow();

	public Terminal() {
		flow.setPrefWidth(Window.stack.getWidth());
		Platform.runLater(() -> Window.stack.getChildren().add(flow));
	}

	public static void println(Object s) {
		printText(s.toString());
		Platform.runLater(() -> flow.getChildren().add(new Text(" \n")));
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void print(Object s) {
		printText(s.toString());
	}

	public static String readln() {
		while (!Window.enterKeyPressed) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Window.enterKeyPressed = false;
		TextFlow tempFlow = flow;
		Platform.runLater(() -> Window.stack.getChildren().remove(tempFlow));
		TextFlow newFlow = new TextFlow();
		newFlow.setPrefWidth(Window.root.getWidth()/2);
		Platform.runLater(() -> Window.stack.getChildren().add(tempFlow));
		Platform.runLater(() -> Window.stack.getChildren().add(newFlow));

		flow = newFlow;
		return Window.s;
	}

	@SuppressWarnings("restriction")
	public static void printText(String s) {
		s = s.replace("(", "∆").replace(")", "∆");
		boolean b = false;
		if(s != "") {
			if (s.substring(0,1) == "∆") {
				s = " " + s;
				b = true;
			}
		}
		String[] strs = s.split("∆");
		if(!b) {
		Platform.runLater(() -> {
			Text t = new Text(strs[0]);
			t.setFont(new Font(15));
			flow.getChildren().add(t);

			FadeTransition ft = new FadeTransition(Duration.millis(1000), t);
			ft.setFromValue(0.0);
			ft.setToValue(1.0);
			ft.play();
		});
		}
		for (int i = 1; i < strs.length; i += 2) {
			int n = Integer.parseInt(strs[i]);
			try {
				Thread.sleep(n);
			} catch (InterruptedException e) {
			}

			if(i + 1 != strs.length) {
			try {
				final int o = i;
				Platform.runLater(() -> {
					Text t = new Text(strs[o + 1]);
					t.setFont(new Font(15));
					flow.getChildren().add(t);
					FadeTransition ft = new FadeTransition(Duration.millis(1000), t);
					ft.setFromValue(0.0);
					ft.setToValue(1.0);
					ft.play();
				});
			} catch (Exception e) {
			}
			}

		}
	}
}
