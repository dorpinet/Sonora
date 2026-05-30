package com.dorpine.ui.screens;

import com.dorpine.api.ApiClient;
import com.dorpine.util.Fonts;
import com.dorpine.util.Theme;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.function.Consumer;

public class AuthScreen extends StackPane {
    private final Consumer<String> onAuthSuccess;

    private VBox card;
    private Label titleLabel;
    private Label errorLabel;

    private TextField emailField;
    private PasswordField passwordField;
    private TextField nicknameField;
    private PasswordField confirmPasswordField;
    private TextField codeField;

    private Button primaryBtn;
    private Button sendCodeBtn;
    private Label linkLabel;
    private Label secondaryLink;

    private Mode mode = Mode.LOGIN;

    enum Mode { LOGIN, REGISTER, FORGOT }

    public AuthScreen(Runnable onAuthSuccess) {
        this.onAuthSuccess = (s) -> onAuthSuccess.run();
        build();
    }

    private void build() {
        setStyle("-fx-background-color: " + Theme.GRADIENT_CSS() + ";");

        Region overlay = new Region();
        overlay.setStyle("-fx-background-color: rgba(10,10,18,0.6);");

        // Ping server in background to wake it up
        new Thread(() -> {
            boolean ok = com.dorpine.api.ApiClient.wakeUp();
            Platform.runLater(() -> {
                if (!ok) {
                    showError("Server is waking up. Please wait and try again.");
                }
            });
        }).start();

        card = new VBox(10);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(340);
        card.setMaxWidth(340);
        card.setMinWidth(340);
        card.setMaxHeight(Region.USE_PREF_SIZE);
        card.setPadding(new Insets(24, 24, 20, 24));
        String glassBg = Theme.isDark() ? "rgba(30,30,45,0.55)" : "rgba(255,255,255,0.55)";
        String glassBorder = Theme.isDark() ? "rgba(255,255,255,0.18)" : "rgba(255,255,255,0.8)";
        card.setStyle(String.join(";",
            "-fx-background-color: " + glassBg,
            "-fx-background-radius: 20px",
            "-fx-border-color: " + glassBorder,
            "-fx-border-radius: 20px",
            "-fx-border-width: 1px"
        ));

        titleLabel = new Label("Login");
        titleLabel.setFont(Fonts.heading(22));
        titleLabel.setStyle("-fx-text-fill: " + Theme.toCss(Theme.textPrimary()) + ";");
        titleLabel.setAlignment(Pos.CENTER);

        errorLabel = new Label("");
        errorLabel.setFont(Fonts.body(12));
        errorLabel.setStyle("-fx-text-fill: #EF4444;");
        errorLabel.setWrapText(true);
        errorLabel.setAlignment(Pos.CENTER);
        errorLabel.setMaxWidth(Double.MAX_VALUE);
        errorLabel.setVisible(false);

        emailField = createField("Email");
        passwordField = createPasswordField("Password");
        nicknameField = createField("Nickname");
        confirmPasswordField = createPasswordField("Confirm password");
        codeField = createField("Enter code");

        sendCodeBtn = createOutlineBtn("Send via email");
        sendCodeBtn.setOnAction(e -> handleSendCode());

        primaryBtn = createPrimaryBtn("Login");
        primaryBtn.setOnAction(e -> handlePrimary());

        linkLabel = createLink("Don't have an account?");
        linkLabel.setOnMouseClicked(e -> switchMode(Mode.REGISTER));

        secondaryLink = createLink("Forgot password?");
        secondaryLink.setOnMouseClicked(e -> switchMode(Mode.FORGOT));

        VBox fieldsBox = new VBox(8);
        fieldsBox.setAlignment(Pos.TOP_CENTER);
        fieldsBox.setFillWidth(true);
        fieldsBox.getChildren().addAll(
            emailField, passwordField, nicknameField,
            confirmPasswordField, sendCodeBtn, codeField, primaryBtn
        );

        HBox linksBox = new HBox(14);
        linksBox.setAlignment(Pos.CENTER);
        linksBox.getChildren().addAll(linkLabel, secondaryLink);

        card.getChildren().addAll(titleLabel, errorLabel, fieldsBox, linksBox);

        getChildren().addAll(overlay, card);
        StackPane.setAlignment(card, Pos.CENTER);

        switchMode(Mode.LOGIN);
    }

    private TextField createField(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setFont(Fonts.body(14));
        String bg = Theme.isDark() ? "rgba(255,255,255,0.08)" : "rgba(255,255,255,0.5)";
        f.setStyle(String.join(";",
            "-fx-background-color: " + bg,
            "-fx-background-radius: 12px",
            "-fx-text-fill: " + Theme.toCss(Theme.textPrimary()),
            "-fx-prompt-text-fill: " + Theme.toCss(Theme.textSecondary()),
            "-fx-padding: 10 14",
            "-fx-border-width: 0"
        ));
        f.setPrefHeight(38);
        f.setMaxWidth(Double.MAX_VALUE);
        return f;
    }

    private PasswordField createPasswordField(String prompt) {
        PasswordField f = new PasswordField();
        f.setPromptText(prompt);
        f.setFont(Fonts.body(14));
        String bg = Theme.isDark() ? "rgba(255,255,255,0.08)" : "rgba(255,255,255,0.5)";
        f.setStyle(String.join(";",
            "-fx-background-color: " + bg,
            "-fx-background-radius: 12px",
            "-fx-text-fill: " + Theme.toCss(Theme.textPrimary()),
            "-fx-prompt-text-fill: " + Theme.toCss(Theme.textSecondary()),
            "-fx-padding: 10 14",
            "-fx-border-width: 0"
        ));
        f.setPrefHeight(38);
        f.setMaxWidth(Double.MAX_VALUE);
        return f;
    }

    private Button createPrimaryBtn(String text) {
        Button b = new Button(text);
        b.setFont(Fonts.heading(14));
        b.setStyle(String.join(";",
            "-fx-background-color: " + Theme.toCss(Theme.accent()),
            "-fx-text-fill: #FFFFFF",
            "-fx-background-radius: 24px",
            "-fx-padding: 10 20",
            "-fx-cursor: hand",
            "-fx-font-weight: bold"
        ));
        b.setMaxWidth(Double.MAX_VALUE);
        b.setPrefHeight(42);
        return b;
    }

    private Button createOutlineBtn(String text) {
        Button b = new Button(text);
        b.setFont(Fonts.body(13));
        String accent = Theme.toCss(Theme.accent());
        b.setStyle(String.join(";",
            "-fx-background-color: transparent",
            "-fx-text-fill: " + accent,
            "-fx-background-radius: 24px",
            "-fx-padding: 8 20",
            "-fx-cursor: hand",
            "-fx-border-color: " + accent,
            "-fx-border-radius: 24px",
            "-fx-border-width: 1.5px"
        ));
        b.setMaxWidth(Double.MAX_VALUE);
        b.setPrefHeight(36);
        return b;
    }

    private Label createLink(String text) {
        Label l = new Label(text);
        l.setFont(Fonts.body(12));
        l.setStyle("-fx-text-fill: " + Theme.toCss(Theme.accent()) + "; -fx-cursor: hand;");
        return l;
    }

    private void switchMode(Mode newMode) {
        this.mode = newMode;
        errorLabel.setVisible(false);
        errorLabel.setText("");

        emailField.clear();
        passwordField.clear();
        nicknameField.clear();
        confirmPasswordField.clear();
        codeField.clear();

        emailField.setVisible(true);
        passwordField.setVisible(true);
        nicknameField.setVisible(false);
        confirmPasswordField.setVisible(false);
        sendCodeBtn.setVisible(false);
        codeField.setVisible(false);
        secondaryLink.setVisible(true);

        switch (newMode) {
            case LOGIN -> {
                titleLabel.setText("Login");
                primaryBtn.setText("Login");
                linkLabel.setText("Don't have an account?");
                linkLabel.setOnMouseClicked(e -> switchMode(Mode.REGISTER));
                secondaryLink.setText("Forgot password?");
                secondaryLink.setOnMouseClicked(e -> switchMode(Mode.FORGOT));
            }
            case REGISTER -> {
                titleLabel.setText("Sign Up");
                primaryBtn.setText("Sign Up");
                linkLabel.setText("Already have an account?");
                linkLabel.setOnMouseClicked(e -> switchMode(Mode.LOGIN));
                secondaryLink.setVisible(false);
                nicknameField.setVisible(true);
                confirmPasswordField.setVisible(true);
                sendCodeBtn.setVisible(true);
                codeField.setVisible(true);
                VBox parent = (VBox) emailField.getParent();
                parent.getChildren().clear();
                parent.getChildren().addAll(nicknameField, passwordField, confirmPasswordField, emailField, sendCodeBtn, codeField, primaryBtn);
            }
            case FORGOT -> {
                titleLabel.setText("Reset Password");
                primaryBtn.setText("Reset Password");
                linkLabel.setText("Back to login");
                linkLabel.setOnMouseClicked(e -> switchMode(Mode.LOGIN));
                secondaryLink.setVisible(false);
                passwordField.setVisible(true);
                confirmPasswordField.setVisible(true);
                sendCodeBtn.setVisible(true);
                codeField.setVisible(true);
                VBox parent = (VBox) emailField.getParent();
                parent.getChildren().clear();
                parent.getChildren().addAll(emailField, sendCodeBtn, codeField, passwordField, confirmPasswordField, primaryBtn);
            }
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }

    private void clearError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }

    private void handleSendCode() {
        clearError();
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            showError("Email is required"); return;
        }
        sendCodeBtn.setDisable(true);
        sendCodeBtn.setText("Sending...");
        new Thread(() -> {
            ApiClient.AuthResult result;
            if (mode == Mode.REGISTER) {
                result = ApiClient.registerInit(email);
            } else {
                result = ApiClient.forgotPassword(email);
            }
            Platform.runLater(() -> {
                sendCodeBtn.setDisable(false);
                sendCodeBtn.setText("Send via email");
                if (result.success) {
                    showError("Code sent to " + email);
                    errorLabel.setStyle("-fx-text-fill: #22C55E;");
                } else {
                    showError(result.error);
                    errorLabel.setStyle("-fx-text-fill: #EF4444;");
                }
            });
        }).start();
    }

    private void handlePrimary() {
        clearError();
        errorLabel.setStyle("-fx-text-fill: #EF4444;");
        switch (mode) {
            case LOGIN -> doLogin();
            case REGISTER -> doRegister();
            case FORGOT -> doReset();
        }
    }

    private void doLogin() {
        String email = emailField.getText().trim();
        String pass = passwordField.getText();
        if (email.isEmpty() || pass.isEmpty()) {
            showError("Email and password are required"); return;
        }
        primaryBtn.setDisable(true);
        primaryBtn.setText("Logging in...");
        new Thread(() -> {
            ApiClient.AuthResult result = ApiClient.login(email, pass);
            Platform.runLater(() -> {
                primaryBtn.setDisable(false);
                primaryBtn.setText("Login");
                if (result.success) {
                    onAuthSuccess.accept("home");
                } else {
                    showError(result.error);
                }
            });
        }).start();
    }

    private void doRegister() {
        String email = emailField.getText().trim();
        String pass = passwordField.getText();
        String confirm = confirmPasswordField.getText();
        String username = nicknameField.getText().trim();
        String code = codeField.getText().trim();
        if (email.isEmpty() || pass.isEmpty() || username.isEmpty() || code.isEmpty()) {
            showError("All fields are required"); return;
        }
        if (!pass.equals(confirm)) {
            showError("Passwords do not match"); return;
        }
        primaryBtn.setDisable(true);
        primaryBtn.setText("Signing up...");
        new Thread(() -> {
            ApiClient.AuthResult result = ApiClient.registerVerify(email, code, username, pass, confirm);
            Platform.runLater(() -> {
                primaryBtn.setDisable(false);
                primaryBtn.setText("Sign Up");
                if (result.success) {
                    onAuthSuccess.accept("home");
                } else {
                    showError(result.error);
                }
            });
        }).start();
    }

    private void doReset() {
        String email = emailField.getText().trim();
        String code = codeField.getText().trim();
        String pass = passwordField.getText();
        String confirm = confirmPasswordField.getText();
        if (email.isEmpty() || code.isEmpty() || pass.isEmpty()) {
            showError("All fields are required"); return;
        }
        if (!pass.equals(confirm)) {
            showError("Passwords do not match"); return;
        }
        primaryBtn.setDisable(true);
        primaryBtn.setText("Resetting...");
        new Thread(() -> {
            ApiClient.AuthResult result = ApiClient.resetPassword(email, code, pass, confirm);
            Platform.runLater(() -> {
                primaryBtn.setDisable(false);
                primaryBtn.setText("Reset Password");
                if (result.success) {
                    showError("Password reset successful. Please log in.");
                    errorLabel.setStyle("-fx-text-fill: #22C55E;");
                    switchMode(Mode.LOGIN);
                } else {
                    showError(result.error);
                }
            });
        }).start();
    }
}
