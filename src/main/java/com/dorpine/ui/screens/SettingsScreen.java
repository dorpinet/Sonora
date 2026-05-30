package com.dorpine.ui.screens;

import com.dorpine.api.ApiClient;
import com.dorpine.model.UserProfile;
import com.dorpine.util.Fonts;
import com.dorpine.util.Session;
import com.dorpine.util.Theme;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.function.Consumer;

public class SettingsScreen extends StackPane {
    private final Consumer<String> navHandler;
    private final Runnable themeToggleHandler;
    private Label usernameLabel;
    private Label emailLabel;

    public SettingsScreen(Consumer<String> navHandler, Runnable themeToggleHandler) {
        this.navHandler = navHandler;
        this.themeToggleHandler = themeToggleHandler;
        build();
    }

    private void build() {
        setStyle("-fx-background-color: " + Theme.GRADIENT_CSS() + ";");

        VBox mainBox = new VBox(16);
        mainBox.setAlignment(Pos.TOP_LEFT);
        mainBox.setPadding(new Insets(20, 24, 24, 24));
        mainBox.setFillWidth(true);

        com.dorpine.ui.components.TopBar topBar = new com.dorpine.ui.components.TopBar(navHandler, themeToggleHandler);
        topBar.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(topBar, Priority.NEVER);

        HBox contentBox = new HBox(24);
        contentBox.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(contentBox, Priority.ALWAYS);
        VBox.setVgrow(contentBox, Priority.ALWAYS);
        contentBox.setMaxWidth(Double.MAX_VALUE);

        UserProfile user = Session.getCurrentUser();
        String displayName = user != null && user.getUsername() != null ? "@" + user.getUsername() : "@guest";
        String displayEmail = user != null && user.getEmail() != null ? user.getEmail() : "";

        VBox userCard = new VBox(10);
        userCard.setAlignment(Pos.TOP_CENTER);
        userCard.setPadding(new Insets(16));
        userCard.setStyle(String.join(";",
            "-fx-background-color: " + (Theme.isDark() ? "rgba(255,255,255,0.08)" : "rgba(255,255,255,0.45)"),
            "-fx-background-radius: 20px",
            "-fx-border-color: " + (Theme.isDark() ? "rgba(255,255,255,0.15)" : "rgba(255,255,255,0.7)"),
            "-fx-border-radius: 20px",
            "-fx-border-width: 1px"
        ));
        userCard.setPrefWidth(240);
        userCard.setMaxWidth(280);
        userCard.setMinWidth(220);
        userCard.setMaxHeight(Region.USE_PREF_SIZE);

        StackPane avatarPane = new StackPane();
        avatarPane.setPrefSize(160, 160);
        avatarPane.setMinSize(160, 160);
        avatarPane.setMaxSize(160, 160);
        avatarPane.setStyle("-fx-background-color: " + Theme.toCss(Color.web("rgba(200,190,255,0.2)")) + "; -fx-background-radius: 18px;");

        String avatarUrl = user != null && user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()
            ? user.getAvatarUrl()
            : "https://pub-ce6ebc870321431c9898f9f710f9cf10.r2.dev/Noicon%20(1).jpg";
        try {
            Image img = new Image(avatarUrl, 160, 160, true, true, true);
            ImageView iv = new ImageView(img);
            iv.setFitWidth(160);
            iv.setFitHeight(160);
            iv.setPreserveRatio(true);
            Rectangle c = new Rectangle(160, 160);
            c.setArcWidth(18);
            c.setArcHeight(18);
            iv.setClip(c);
            avatarPane.getChildren().add(iv);
        } catch (Exception e) {
            Label placeholder = new Label("U");
            placeholder.setFont(Fonts.heading(64));
            placeholder.setStyle("-fx-text-fill: " + Theme.toCss(Theme.accent()) + ";");
            avatarPane.getChildren().add(placeholder);
        }

        usernameLabel = new Label(displayName);
        usernameLabel.setFont(Fonts.heading(22));
        usernameLabel.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textPrimary()) + ";");
        usernameLabel.setAlignment(Pos.CENTER);

        emailLabel = new Label(displayEmail);
        emailLabel.setFont(Fonts.body(12));
        emailLabel.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textSecondary()) + ";");
        emailLabel.setAlignment(Pos.CENTER);

        userCard.getChildren().addAll(avatarPane, usernameLabel, emailLabel);

        StackPane settingsPanel = new StackPane();
        settingsPanel.setStyle(String.join(";",
            "-fx-background-color: " + (Theme.isDark() ? "rgba(255,255,255,0.08)" : "rgba(255,255,255,0.45)"),
            "-fx-background-radius: 20px",
            "-fx-border-color: " + (Theme.isDark() ? "rgba(255,255,255,0.15)" : "rgba(255,255,255,0.7)"),
            "-fx-border-radius: 20px",
            "-fx-border-width: 1px"
        ));
        settingsPanel.setPadding(new Insets(24));
        HBox.setHgrow(settingsPanel, Priority.ALWAYS);

        VBox settingsBox = new VBox(4);
        settingsBox.setAlignment(Pos.TOP_CENTER);
        settingsBox.setFillWidth(true);

        settingsBox.getChildren().add(settingRow("Change username", this::changeUsername));
        settingsBox.getChildren().add(settingRow("Change email", this::changeEmail));
        settingsBox.getChildren().add(settingRow("Change password", this::changePassword));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        settingsBox.getChildren().add(spacer);

        Label logout = new Label("Logout");
        logout.setFont(Fonts.heading(18));
        logout.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textPrimary()) + "; -fx-cursor: hand;");
        logout.setPadding(new Insets(12, 0, 0, 0));
        logout.setOnMouseClicked(e -> {
            com.dorpine.api.ApiClient.logout();
            navHandler.accept("logout");
        });
        settingsBox.getChildren().add(logout);

        settingsPanel.getChildren().add(settingsBox);

        contentBox.getChildren().addAll(userCard, settingsPanel);

        mainBox.getChildren().addAll(topBar, contentBox);
        getChildren().add(mainBox);
    }

    private HBox settingRow(String text, Runnable onClick) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(16, 12, 16, 12));
        row.setStyle(String.join(";",
            "-fx-background-color: transparent",
            "-fx-border-color: " + (Theme.isDark() ? "rgba(255,255,255,0.1)" : "rgba(255,255,255,0.5)"),
            "-fx-border-width: 0 0 1 0"
        ));

        Label lbl = new Label(text);
        lbl.setFont(Fonts.heading(18));
        lbl.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textPrimary()) + ";");
        HBox.setHgrow(lbl, Priority.ALWAYS);

        Label arrow = new Label("\u2192");
        arrow.setFont(Fonts.body(22));
        arrow.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textPrimary()) + "; -fx-cursor: hand;");

        row.setOnMouseClicked(e -> onClick.run());
        arrow.setOnMouseClicked(e -> onClick.run());
        lbl.setOnMouseClicked(e -> onClick.run());

        row.getChildren().addAll(lbl, arrow);
        return row;
    }

    private void changeUsername() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change username");
        dialog.setHeaderText(null);
        dialog.setContentText("New username:");
        TextField passField = new PasswordField();
        passField.setPromptText("Current password");
        passField.setStyle("-fx-background-radius: 12px; -fx-padding: 8 12;");
        VBox pane = new VBox(8, dialog.getEditor(), passField);
        pane.setPadding(new Insets(10, 0, 0, 0));
        dialog.getDialogPane().setContent(pane);
        dialog.showAndWait().ifPresent(val -> {
            String password = passField.getText();
            if (val.trim().isEmpty() || password.isEmpty()) return;
            new Thread(() -> {
                ApiClient.AuthResult r = ApiClient.updateUsername(val.trim(), password);
                Platform.runLater(() -> {
                    if (r.success) {
                        usernameLabel.setText("@" + val.trim());
                        UserProfile u = Session.getCurrentUser();
                        if (u != null) u.setUsername(val.trim());
                    }
                });
            }).start();
        });
    }

    private void changeEmail() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change email");
        dialog.setHeaderText(null);
        dialog.setContentText("New email:");
        TextField passField = new PasswordField();
        passField.setPromptText("Current password");
        passField.setStyle("-fx-background-radius: 12px; -fx-padding: 8 12;");
        VBox pane = new VBox(8, dialog.getEditor(), passField);
        pane.setPadding(new Insets(10, 0, 0, 0));
        dialog.getDialogPane().setContent(pane);
        dialog.showAndWait().ifPresent(val -> {
            String password = passField.getText();
            if (val.trim().isEmpty() || password.isEmpty()) return;
            new Thread(() -> {
                ApiClient.AuthResult r = ApiClient.changeEmailInit(val.trim(), password);
                Platform.runLater(() -> {
                    if (r.success) {
                        TextInputDialog codeDialog = new TextInputDialog();
                        codeDialog.setTitle("Verify email");
                        codeDialog.setHeaderText("Code sent to " + val.trim());
                        codeDialog.setContentText("Enter code:");
                        codeDialog.showAndWait().ifPresent(code -> {
                            if (code.trim().isEmpty()) return;
                            new Thread(() -> {
                                ApiClient.AuthResult r2 = ApiClient.changeEmailVerify(val.trim(), code.trim());
                                Platform.runLater(() -> {
                                    if (r2.success) {
                                        emailLabel.setText(val.trim());
                                        UserProfile u = Session.getCurrentUser();
                                        if (u != null) u.setEmail(val.trim());
                                    }
                                });
                            }).start();
                        });
                    }
                });
            }).start();
        });
    }

    private void changePassword() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Change password");
        dialog.setHeaderText(null);
        PasswordField current = new PasswordField(); current.setPromptText("Current password");
        PasswordField newPass = new PasswordField(); newPass.setPromptText("New password");
        PasswordField confirm = new PasswordField(); confirm.setPromptText("Confirm password");
        VBox pane = new VBox(8, current, newPass, confirm);
        pane.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(pane);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(btn -> btn == ButtonType.OK ? new String[]{current.getText(), newPass.getText(), confirm.getText()} : null);
        dialog.showAndWait().ifPresent(arr -> {
            if (arr[0].isEmpty() || arr[1].isEmpty()) return;
            new Thread(() -> {
                ApiClient.AuthResult r = ApiClient.changePassword(arr[0], arr[1], arr[2]);
                Platform.runLater(() -> {});
            }).start();
        });
    }
}
