import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * ============================================================================
 * PASSWORD PRO - ULTIMATE SECURITY SUITE (STANDALONE EDITION)
 * ============================================================================
 * Developer: Mohmmad Sameer
 * College: Adarsh College of Professional Studies
 * 
 * New Ultra-Intuitive Design:
 *   - 1-Click Interactive Theme Switcher (Sky Blue, Emerald Mint, Royal Violet, Sunset Amber)
 *   - Sleek, Symmetrically Centered & Modern Login Screen
 *   - Clean, Simple, Uncluttered Layout - Easy for Anyone to Understand Immediately
 *   - Hero Generator Card: Big Password Box + Big Generate & Copy Buttons
 *   - Clean Tabs: [ 1-Click Presets ] [ Saved Vault ] [ Bulk Generator ]
 *   - 100% Pure Black Text on every element for maximum clarity
 *   - Zero emojis or square character glyphs (100% clean English text)
 *   - Animated Red Car Projector Headlight Eyes Vector Canvas
 *   - CSPRNG (SecureRandom), Diceware, and Numeric PIN generation modes
 *   - Shannon Entropy Engine & Realistic Offline GPU Crack-Time Estimator
 *   - NATO Phonetic Alphabet Speller for phone verification
 *   - Bulk Password Generator (5 to 50 passwords with 1-click copy)
 *   - Offline Password Vault with Live Search Filter & TXT File Export
 * ============================================================================
 */
public class PasswordProApp {

    private static JFrame mainFrame;
    private static JPanel cardPanel;
    private static CardLayout cardLayout;

    // Login Screen Components
    private static JPanel loginRootPanel;
    private static JPanel loginCardPanel;
    private static JTextField userField;
    private static JPasswordField passField;
    private static JCheckBox rememberMeBox;
    private static JLabel loginStatusLabel;
    private static final String CREDENTIALS_FILE = "credentials.properties";

    // Dashboard Components
    private static JPanel dashboardRootPanel;
    private static JPanel heroCardPanel;
    private static JPanel controlsCardPanel;
    private static JTabbedPane toolsTabbedPane;
    private static JButton themeSwitchBtn;

    // Output & Metrics
    private static JTextField passwordDisplay;
    private static JLabel charBreakdownLabel;
    private static JSlider lengthSlider;
    private static JLabel lengthDisplayLabel;
    private static JLabel strengthLabel;
    private static JProgressBar strengthProgressBar;
    private static JLabel entropyBitsLabel;
    private static JLabel crackTimeLabel;
    private static JLabel clipboardStatusBadge;

    // Core Checkboxes
    private static JCheckBox chkUpper, chkLower, chkNumbers, chkSymbols, chkAmbiguous, chkNoRepeat;

    // Modes
    private static JRadioButton modeRandomRadio, modePassphraseRadio, modePinRadio;

    // Vault Components
    private static DefaultListModel<String> allHistoryModel;
    private static DefaultListModel<String> filteredListModel;
    private static JList<String> recentList;
    private static JTextField searchHistoryField;
    private static JLabel historyCountLabel;

    // Bulk Modal components
    private static JTextArea bulkTextArea;
    private static JSpinner bulkSpinner;

    private static boolean isPasswordMasked = false;
    private static String rawCurrentPassword = "";

    // ========================================================================
    // THEME ENGINE (4 DISTINCT BEAUTIFUL THEMES)
    // ========================================================================
    static class ThemePalette {
        String name;
        Color appBg;
        Color card1Bg;
        Color card1Border;
        Color card2Bg;
        Color card2Border;
        Color card3Bg;
        Color card3Border;
        Color btnPrimaryBg;
        Color btnSecondaryBg;
        Color btnAccentBg;

        public ThemePalette(String name, Color appBg, Color c1Bg, Color c1Brd, Color c2Bg, Color c2Brd,
                            Color c3Bg, Color c3Brd, Color pBg, Color sBg, Color aBg) {
            this.name = name;
            this.appBg = appBg;
            this.card1Bg = c1Bg;
            this.card1Border = c1Brd;
            this.card2Bg = c2Bg;
            this.card2Border = c2Brd;
            this.card3Bg = c3Bg;
            this.card3Border = c3Brd;
            this.btnPrimaryBg = pBg;
            this.btnSecondaryBg = sBg;
            this.btnAccentBg = aBg;
        }
    }

    private static final ThemePalette[] THEMES = {
            new ThemePalette(
                    "Sky Blue Studio",
                    new Color(224, 242, 254), // App BG: Soft Sky 100
                    new Color(186, 230, 253), // Card 1: Sky 200
                    new Color(2, 132, 199),   // Border 1: Sky 600
                    new Color(254, 243, 199), // Card 2: Amber 100
                    new Color(217, 119, 6),   // Border 2: Amber 600
                    new Color(236, 253, 245), // Card 3: Emerald 100
                    new Color(5, 150, 105),   // Border 3: Emerald 600
                    new Color(52, 211, 153),  // Primary: Emerald
                    new Color(125, 211, 252), // Secondary: Cyan
                    new Color(254, 240, 138)  // Accent: Yellow
            ),
            new ThemePalette(
                    "Fresh Emerald Mint",
                    new Color(236, 253, 245), // App BG: Mint 100
                    new Color(167, 243, 208), // Card 1: Mint 200
                    new Color(5, 150, 105),   // Border 1: Forest Green
                    new Color(254, 240, 138), // Card 2: Butter Yellow
                    new Color(202, 138, 4),   // Border 2: Gold
                    new Color(224, 242, 254), // Card 3: Ice Blue
                    new Color(2, 132, 199),   // Border 3: Blue
                    new Color(16, 185, 129),  // Primary: Green
                    new Color(56, 189, 248),  // Secondary: Sky
                    new Color(254, 215, 170)  // Accent: Peach
            ),
            new ThemePalette(
                    "Royal Violet Studio",
                    new Color(245, 243, 255), // App BG: Violet 100
                    new Color(221, 214, 254), // Card 1: Lavender 200
                    new Color(124, 58, 237),  // Border 1: Deep Violet
                    new Color(254, 243, 199), // Card 2: Amber
                    new Color(217, 119, 6),   // Border 2: Amber
                    new Color(236, 253, 245), // Card 3: Mint
                    new Color(5, 150, 105),   // Border 3: Mint
                    new Color(192, 132, 252), // Primary: Purple
                    new Color(125, 211, 252), // Secondary: Blue
                    new Color(254, 240, 138)  // Accent: Yellow
            ),
            new ThemePalette(
                    "Warm Sunset Amber",
                    new Color(255, 251, 235), // App BG: Warm Cream
                    new Color(254, 215, 170), // Card 1: Warm Peach
                    new Color(234, 88, 12),   // Border 1: Orange
                    new Color(254, 243, 199), // Card 2: Gold
                    new Color(217, 119, 6),   // Border 2: Amber
                    new Color(224, 242, 254), // Card 3: Sky
                    new Color(2, 132, 199),   // Border 3: Sky
                    new Color(251, 146, 60),  // Primary: Orange
                    new Color(52, 211, 153),  // Secondary: Green
                    new Color(250, 204, 21)   // Accent: Yellow
            )
    };

    private static int currentThemeIndex = 0;

    // Diceware Word Dictionary for Passphrases
    private static final String[] WORD_DICTIONARY = {
            "apple", "anchor", "arrow", "atomic", "beacon", "breeze", "bridge", "castle",
            "canyon", "comet", "crater", "crypto", "danger", "desert", "dragon", "eagle",
            "echo", "ember", "falcon", "forest", "galaxy", "glacier", "harbor", "hawk",
            "horizon", "island", "jungle", "jupiter", "knight", "laser", "legacy", "lotus",
            "lunar", "magnet", "matrix", "meadow", "meteor", "nebula", "ninja", "oasis",
            "ocean", "orbit", "ozone", "palace", "panther", "phoenix", "planet", "prism",
            "quantum", "radar", "raven", "reef", "river", "rocket", "ruby", "safari",
            "saturn", "shadow", "shield", "silver", "solar", "spark", "sphinx", "spirit",
            "spring", "stellar", "sunset", "thunder", "tiger", "titan", "topaz", "valley",
            "vapor", "velvet", "vortex", "voyage", "vulcan", "willow", "winter", "wizard",
            "wolf", "zenith", "zephyr", "amber", "arctic", "aurora", "blizzard", "bronze",
            "cascade", "celestial", "chrono", "cipher", "clover", "cobalt", "copper", "crystal",
            "delta", "dynasty", "enigma", "everest", "fusion", "glimmer", "haven", "hyperion",
            "infinity", "iron", "jasper", "kinetic", "mirage", "monolith", "mystic", "nova",
            "obsidian", "omega", "onyx", "orion", "pebble", "plasma", "polaris", "pyramid",
            "radiant", "relic", "resonance", "samurai", "sapphire", "solstice", "specter", "tempest"
    };

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(PasswordProApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        mainFrame = new JFrame("Password Pro - Ultimate Security Suite");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(750, 950);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(true);
        mainFrame.setMinimumSize(new Dimension(700, 860));

        // App Icon
        mainFrame.setIconImage(createAppIcon());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(createLoginScreen(), "LOGIN");
        cardPanel.add(createDashboardScreen(), "DASHBOARD");

        mainFrame.add(cardPanel);
        mainFrame.setVisible(true);

        loadCredentials();
    }

    // ========================================================================
    // SCREEN 1: MODERN, SLEEK, AESTHETIC & 100% CENTERED LOGIN SCREEN
    // ========================================================================
    private static JPanel createLoginScreen() {
        ThemePalette t = THEMES[currentThemeIndex];
        loginRootPanel = new JPanel(new BorderLayout());
        loginRootPanel.setBackground(t.appBg);

        // Animated Car Eyes Header at Top
        AnimatedCarEyeHeader header = new AnimatedCarEyeHeader("PASSWORD PRO - SECURITY SUITE", Color.RED, t.appBg, 2);
        header.setPreferredSize(new Dimension(750, 75));
        loginRootPanel.add(header, BorderLayout.NORTH);

        // Center Container
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        loginCardPanel = new JPanel();
        loginCardPanel.setLayout(new BoxLayout(loginCardPanel, BoxLayout.Y_AXIS));
        loginCardPanel.setPreferredSize(new Dimension(490, 620));
        loginCardPanel.setBackground(Color.WHITE);
        loginCardPanel.setBorder(new CompoundBorder(
                new LineBorder(t.card1Border, 3, true),
                new EmptyBorder(24, 35, 24, 35)
        ));

        // Security Pill Badge (Centered)
        JLabel securityPill = new JLabel("SECURE ACCESS GATEWAY", SwingConstants.CENTER);
        securityPill.setFont(new Font("Segoe UI", Font.BOLD, 11));
        securityPill.setForeground(Color.BLACK);
        securityPill.setOpaque(true);
        securityPill.setBackground(new Color(224, 242, 254)); // Soft Sky
        securityPill.setBorder(new CompoundBorder(
                new LineBorder(new Color(2, 132, 199), 1, true),
                new EmptyBorder(4, 16, 4, 16)
        ));
        securityPill.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginCardPanel.add(securityPill);

        loginCardPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Main App Title (Centered)
        JLabel loginTitle = new JLabel("USER AUTHENTICATION", SwingConstants.CENTER);
        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        loginTitle.setForeground(Color.BLACK);
        loginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginCardPanel.add(loginTitle);

        loginCardPanel.add(Box.createRigidArea(new Dimension(0, 4)));

        JLabel loginSubtitle = new JLabel("Enter your login credentials or use instant Guest Access", SwingConstants.CENTER);
        loginSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loginSubtitle.setForeground(Color.BLACK);
        loginSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginCardPanel.add(loginSubtitle);

        loginCardPanel.add(Box.createRigidArea(new Dimension(0, 18)));

        // Form Container (Width: 380px, strictly centered)
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setOpaque(false);
        formContainer.setMaximumSize(new Dimension(380, 470));
        formContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username Label (Centered)
        JLabel userLabel = new JLabel("USERNAME", SwingConstants.CENTER);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLabel.setForeground(Color.BLACK);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formContainer.add(userLabel);
        formContainer.add(Box.createRigidArea(new Dimension(0, 4)));

        userField = new JTextField();
        userField.setHorizontalAlignment(JTextField.CENTER);
        userField.setFont(new Font("Segoe UI", Font.BOLD, 15));
        userField.setForeground(Color.BLACK);
        userField.setCaretColor(Color.BLACK);
        userField.setBackground(new Color(254, 243, 199)); // Soft Cream / Amber 100
        userField.setBorder(new CompoundBorder(
                new LineBorder(new Color(217, 119, 6), 2, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        userField.setMaximumSize(new Dimension(380, 42));
        userField.setPreferredSize(new Dimension(380, 42));
        userField.setAlignmentX(Component.CENTER_ALIGNMENT);
        formContainer.add(userField);

        formContainer.add(Box.createRigidArea(new Dimension(0, 14)));

        // Password Label (Centered)
        JLabel passLabel = new JLabel("PASSWORD", SwingConstants.CENTER);
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setForeground(Color.BLACK);
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formContainer.add(passLabel);
        formContainer.add(Box.createRigidArea(new Dimension(0, 4)));

        passField = new JPasswordField();
        passField.setHorizontalAlignment(JTextField.CENTER);
        passField.setFont(new Font("Segoe UI", Font.BOLD, 15));
        passField.setForeground(Color.BLACK);
        passField.setCaretColor(Color.BLACK);
        passField.setBackground(new Color(254, 243, 199)); // Soft Cream / Amber 100
        passField.setBorder(new CompoundBorder(
                new LineBorder(new Color(217, 119, 6), 2, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        passField.setMaximumSize(new Dimension(380, 42));
        passField.setPreferredSize(new Dimension(380, 42));
        passField.setAlignmentX(Component.CENTER_ALIGNMENT);
        formContainer.add(passField);

        formContainer.add(Box.createRigidArea(new Dimension(0, 8)));

        // Show Password Button (Centered)
        JPanel toggleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        toggleRow.setOpaque(false);
        JButton showPassBtn = createStyledButton("Show Password", new Color(254, 215, 170), new Color(251, 146, 60));
        showPassBtn.setPreferredSize(new Dimension(140, 28));
        showPassBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        showPassBtn.addActionListener(e -> {
            if (passField.getEchoChar() == (char) 0) {
                passField.setEchoChar('\u2022');
                showPassBtn.setText("Show Password");
            } else {
                passField.setEchoChar((char) 0);
                showPassBtn.setText("Hide Password");
            }
        });
        toggleRow.add(showPassBtn);
        formContainer.add(toggleRow);

        formContainer.add(Box.createRigidArea(new Dimension(0, 8)));

        // Remember Me Checkbox (Centered)
        JPanel remPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        remPanel.setOpaque(false);
        rememberMeBox = new JCheckBox("Remember Credentials on this PC");
        rememberMeBox.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rememberMeBox.setForeground(Color.BLACK);
        rememberMeBox.setOpaque(false);
        rememberMeBox.setFocusPainted(false);
        remPanel.add(rememberMeBox);
        formContainer.add(remPanel);

        formContainer.add(Box.createRigidArea(new Dimension(0, 8)));

        // Status Feedback Label (Centered)
        loginStatusLabel = new JLabel(" ", SwingConstants.CENTER);
        loginStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginStatusLabel.setForeground(Color.BLACK);
        loginStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formContainer.add(loginStatusLabel);

        formContainer.add(Box.createRigidArea(new Dimension(0, 8)));

        // Login Button (Centered)
        JButton loginBtn = createStyledButton("LOGIN TO DASHBOARD", new Color(52, 211, 153), new Color(16, 185, 129));
        loginBtn.setMaximumSize(new Dimension(380, 46));
        loginBtn.setPreferredSize(new Dimension(380, 46));
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> performLogin());
        formContainer.add(loginBtn);

        formContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        // Guest Access Button (Centered)
        JButton guestBtn = createStyledButton("GUEST ACCESS (INSTANT LAUNCH)", new Color(254, 240, 138), new Color(250, 204, 21));
        guestBtn.setMaximumSize(new Dimension(380, 42));
        guestBtn.setPreferredSize(new Dimension(380, 42));
        guestBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        guestBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        guestBtn.addActionListener(e -> {
            playClickSound();
            cardLayout.show(cardPanel, "DASHBOARD");
            if (rawCurrentPassword.isEmpty()) {
                generatePassword();
            }
        });
        formContainer.add(guestBtn);

        formContainer.add(Box.createRigidArea(new Dimension(0, 14)));

        // Security Tips Box (Centered)
        JPanel tipCard = new JPanel(new GridLayout(2, 1, 2, 2));
        tipCard.setBackground(new Color(224, 242, 254));
        tipCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(56, 189, 248), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        tipCard.setMaximumSize(new Dimension(380, 52));
        tipCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tip1 = new JLabel("* High-Entropy Security: CSPRNG (SecureRandom) & Diceware", SwingConstants.CENTER);
        tip1.setFont(new Font("Segoe UI", Font.BOLD, 11));
        tip1.setForeground(Color.BLACK);

        JLabel tip2 = new JLabel("* Standalone Java App: Runs on Windows, Mac & Linux offline", SwingConstants.CENTER);
        tip2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        tip2.setForeground(Color.BLACK);

        tipCard.add(tip1);
        tipCard.add(tip2);
        formContainer.add(tipCard);

        loginCardPanel.add(formContainer);

        // Enter key listeners
        userField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) passField.requestFocusInWindow();
            }
        });
        passField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) performLogin();
            }
        });

        centerWrapper.add(loginCardPanel);
        loginRootPanel.add(centerWrapper, BorderLayout.CENTER);

        return loginRootPanel;
    }

    private static void performLogin() {
        playClickSound();
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            loginStatusLabel.setText("Please enter both Username and Password!");
            loginStatusLabel.setForeground(new Color(185, 28, 28));
            return;
        }

        if (rememberMeBox.isSelected()) {
            saveCredentials(user, pass);
        }

        loginStatusLabel.setText("");
        cardLayout.show(cardPanel, "DASHBOARD");
        if (rawCurrentPassword.isEmpty()) {
            generatePassword();
        }
    }

    // ========================================================================
    // SCREEN 2: MAIN DASHBOARD (ULTRA-CLEAN, INTUITIVE & THEME-SWITCHABLE)
    // ========================================================================
    private static JPanel createDashboardScreen() {
        ThemePalette t = THEMES[currentThemeIndex];
        dashboardRootPanel = new JPanel(new BorderLayout());
        dashboardRootPanel.setBackground(t.appBg);

        // --- TOP NAVIGATION BAR ---
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(t.appBg);
        topHeader.setBorder(new EmptyBorder(6, 14, 6, 14));

        AnimatedCarEyeHeader carHeader = new AnimatedCarEyeHeader("PASSWORD PRO - SECURITY CENTER", Color.RED, t.appBg, 4);
        carHeader.setPreferredSize(new Dimension(410, 50));
        topHeader.add(carHeader, BorderLayout.WEST);

        // Action Buttons: Theme Switcher, Bulk, Logout
        JPanel topActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        topActions.setOpaque(false);

        themeSwitchBtn = createStyledButton("Theme: " + t.name, t.card2Bg, t.card1Bg);
        themeSwitchBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        themeSwitchBtn.setToolTipText("Click to cycle between 4 attractive color themes");
        themeSwitchBtn.addActionListener(e -> cycleTheme());
        topActions.add(themeSwitchBtn);

        JButton logoutBtn = createStyledButton("Logout", new Color(254, 202, 202), new Color(248, 113, 113));
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.setToolTipText("Return to user login screen");
        logoutBtn.addActionListener(e -> {
            playClickSound();
            cardLayout.show(cardPanel, "LOGIN");
        });
        topActions.add(logoutBtn);

        topHeader.add(topActions, BorderLayout.EAST);
        dashboardRootPanel.add(topHeader, BorderLayout.NORTH);

        // --- MAIN CONTENT (SCROLLABLE & INTUITIVE) ---
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(t.appBg);
        mainContent.setBorder(new EmptyBorder(6, 16, 16, 16));

        // --------------------------------------------------------------------
        // SECTION 1: HERO PASSWORD RESULT CARD (Large, High Contrast)
        // --------------------------------------------------------------------
        heroCardPanel = new JPanel(new BorderLayout(8, 8));
        heroCardPanel.setBackground(t.card1Bg);
        heroCardPanel.setBorder(new CompoundBorder(
                new TitledBorder(
                        new LineBorder(t.card1Border, 3, true),
                        " YOUR GENERATED PASSWORD ",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        Color.BLACK
                ),
                new EmptyBorder(8, 14, 12, 14)
        ));
        heroCardPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 175));

        // Top Row: Big Password Text Field + Quick Action Buttons
        JPanel passRow = new JPanel(new BorderLayout(8, 0));
        passRow.setOpaque(false);

        passwordDisplay = new JTextField();
        passwordDisplay.setEditable(false);
        passwordDisplay.setFont(new Font("Consolas", Font.BOLD, 22));
        passwordDisplay.setBackground(Color.WHITE);
        passwordDisplay.setForeground(Color.BLACK);
        passwordDisplay.setCaretColor(Color.BLACK);
        passwordDisplay.setBorder(new CompoundBorder(
                new LineBorder(t.card1Border, 2, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
        passRow.add(passwordDisplay, BorderLayout.CENTER);

        JPanel passQuickBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        passQuickBtns.setOpaque(false);

        JButton peekBtn = createStyledButton("Show", new Color(254, 215, 170), new Color(251, 146, 60));
        peekBtn.setPreferredSize(new Dimension(80, 44));
        peekBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        peekBtn.setToolTipText("Toggle visibility between masked asterisks and plain text");
        peekBtn.addActionListener(e -> togglePasswordMask(peekBtn));
        passQuickBtns.add(peekBtn);

        JButton quickCopyBtn = createStyledButton("Copy", new Color(134, 239, 172), new Color(74, 222, 128));
        quickCopyBtn.setPreferredSize(new Dimension(85, 44));
        quickCopyBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        quickCopyBtn.setToolTipText("Copy this password immediately to the system clipboard");
        quickCopyBtn.addActionListener(e -> {
            playClickSound();
            copyToClipboard(rawCurrentPassword);
        });
        passQuickBtns.add(quickCopyBtn);

        JButton reRollBtn = createStyledButton("Refresh", new Color(254, 240, 138), new Color(250, 204, 21));
        reRollBtn.setPreferredSize(new Dimension(92, 44));
        reRollBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        reRollBtn.setToolTipText("Instantly generate another password with current settings");
        reRollBtn.addActionListener(e -> {
            playClickSound();
            generatePassword();
        });
        passQuickBtns.add(reRollBtn);

        passRow.add(passQuickBtns, BorderLayout.EAST);
        heroCardPanel.add(passRow, BorderLayout.NORTH);

        // Middle Row: Visual Strength Health Bar
        JPanel strengthRow = new JPanel(new BorderLayout(8, 4));
        strengthRow.setOpaque(false);

        strengthLabel = new JLabel("Strength: VERY STRONG (100%)", SwingConstants.CENTER);
        strengthLabel.setOpaque(true);
        strengthLabel.setBackground(new Color(134, 239, 172)); // Emerald
        strengthLabel.setForeground(Color.BLACK);
        strengthLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        strengthLabel.setBorder(new LineBorder(new Color(5, 150, 105), 1, true));
        strengthLabel.setPreferredSize(new Dimension(240, 26));
        strengthRow.add(strengthLabel, BorderLayout.WEST);

        strengthProgressBar = new JProgressBar(0, 100);
        strengthProgressBar.setValue(100);
        strengthProgressBar.setBackground(Color.WHITE);
        strengthProgressBar.setForeground(new Color(34, 197, 94));
        strengthProgressBar.setBorder(new LineBorder(Color.BLACK, 1));
        strengthProgressBar.setPreferredSize(new Dimension(200, 26));
        strengthRow.add(strengthProgressBar, BorderLayout.CENTER);

        clipboardStatusBadge = new JLabel("Clipboard: Ready", SwingConstants.CENTER);
        clipboardStatusBadge.setOpaque(true);
        clipboardStatusBadge.setBackground(new Color(254, 240, 138));
        clipboardStatusBadge.setForeground(Color.BLACK);
        clipboardStatusBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        clipboardStatusBadge.setBorder(new LineBorder(new Color(202, 138, 4), 1, true));
        clipboardStatusBadge.setPreferredSize(new Dimension(170, 26));
        strengthRow.add(clipboardStatusBadge, BorderLayout.EAST);

        heroCardPanel.add(strengthRow, BorderLayout.CENTER);

        // Bottom Details: Entropy & Composition
        JPanel detailsRow = new JPanel(new GridLayout(1, 2, 8, 0));
        detailsRow.setOpaque(false);

        charBreakdownLabel = new JLabel("Composition: 16 chars | Upper: 4, Lower: 6, Digits: 3, Symbols: 3");
        charBreakdownLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        charBreakdownLabel.setForeground(Color.BLACK);
        detailsRow.add(charBreakdownLabel);

        crackTimeLabel = new JLabel("Crack Time: 100+ Centuries | Entropy: 95.2 Bits", SwingConstants.RIGHT);
        crackTimeLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        crackTimeLabel.setForeground(Color.BLACK);
        detailsRow.add(crackTimeLabel);

        heroCardPanel.add(detailsRow, BorderLayout.SOUTH);

        mainContent.add(heroCardPanel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 10)));

        // --------------------------------------------------------------------
        // SECTION 2: CORE GENERATION CONTROLS (Big Buttons, Slider, Checkboxes)
        // --------------------------------------------------------------------
        controlsCardPanel = new JPanel();
        controlsCardPanel.setLayout(new BoxLayout(controlsCardPanel, BoxLayout.Y_AXIS));
        controlsCardPanel.setBackground(t.card2Bg);
        controlsCardPanel.setBorder(new CompoundBorder(
                new TitledBorder(
                        new LineBorder(t.card2Border, 3, true),
                        " CORE CONTROLS & RULES ",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 13),
                        Color.BLACK
                ),
                new EmptyBorder(8, 14, 10, 14)
        ));
        controlsCardPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        // Two Large Action Buttons (Generate & Copy)
        JPanel masterBtnsRow = new JPanel(new GridLayout(1, 3, 10, 0));
        masterBtnsRow.setOpaque(false);
        masterBtnsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JButton masterGenBtn = createStyledButton("GENERATE PASSWORD", new Color(52, 211, 153), new Color(16, 185, 129));
        masterGenBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        masterGenBtn.setToolTipText("Click to generate a brand new strong password");
        masterGenBtn.addActionListener(e -> {
            playClickSound();
            generatePassword();
        });

        JButton masterCopyBtn = createStyledButton("COPY TO CLIPBOARD", new Color(125, 211, 252), new Color(56, 189, 248));
        masterCopyBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        masterCopyBtn.setToolTipText("Copy this password to use anywhere");
        masterCopyBtn.addActionListener(e -> {
            playClickSound();
            copyToClipboard(rawCurrentPassword);
        });

        JButton masterNatoBtn = createStyledButton("NATO SPELLER", new Color(254, 240, 138), new Color(250, 204, 21));
        masterNatoBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        masterNatoBtn.setToolTipText("Show phonetic spelling (Alpha, Bravo) for phone calls");
        masterNatoBtn.addActionListener(e -> openNatoModal());

        masterBtnsRow.add(masterGenBtn);
        masterBtnsRow.add(masterCopyBtn);
        masterBtnsRow.add(masterNatoBtn);
        controlsCardPanel.add(masterBtnsRow);

        controlsCardPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Length Slider Row
        JPanel sliderRow = new JPanel(new BorderLayout(8, 0));
        sliderRow.setOpaque(false);

        lengthDisplayLabel = new JLabel("Password Length: 16 characters", SwingConstants.LEFT);
        lengthDisplayLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lengthDisplayLabel.setForeground(Color.BLACK);
        sliderRow.add(lengthDisplayLabel, BorderLayout.WEST);

        lengthSlider = new JSlider(4, 64, 16);
        lengthSlider.setBackground(t.card2Bg);
        lengthSlider.setForeground(Color.BLACK);
        lengthSlider.setMajorTickSpacing(12);
        lengthSlider.setMinorTickSpacing(4);
        lengthSlider.setPaintTicks(true);
        lengthSlider.addChangeListener(e -> {
            if (modePassphraseRadio != null && modePassphraseRadio.isSelected()) {
                lengthDisplayLabel.setText("Password Length: " + lengthSlider.getValue() + " words");
            } else {
                lengthDisplayLabel.setText("Password Length: " + lengthSlider.getValue() + " characters");
            }
            generatePassword();
        });
        sliderRow.add(lengthSlider, BorderLayout.CENTER);

        JPanel plusMinusRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        plusMinusRow.setOpaque(false);

        JButton minusBtn = createStyledButton("-", new Color(254, 202, 202), new Color(248, 113, 113));
        minusBtn.setPreferredSize(new Dimension(36, 28));
        minusBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        minusBtn.addActionListener(e -> {
            if (lengthSlider.getValue() > lengthSlider.getMinimum()) {
                lengthSlider.setValue(lengthSlider.getValue() - 1);
            }
        });

        JButton plusBtn = createStyledButton("+", new Color(187, 247, 208), new Color(134, 239, 172));
        plusBtn.setPreferredSize(new Dimension(36, 28));
        plusBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        plusBtn.addActionListener(e -> {
            if (lengthSlider.getValue() < lengthSlider.getMaximum()) {
                lengthSlider.setValue(lengthSlider.getValue() + 1);
            }
        });

        plusMinusRow.add(minusBtn);
        plusMinusRow.add(plusBtn);
        sliderRow.add(plusMinusRow, BorderLayout.EAST);
        controlsCardPanel.add(sliderRow);

        controlsCardPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        // 4 Clean Checkboxes (Horizontal Row)
        JPanel chkRow = new JPanel(new GridLayout(1, 4, 8, 0));
        chkRow.setOpaque(false);

        chkUpper = createStyledCheckbox("Uppercase (A-Z)", true);
        chkLower = createStyledCheckbox("Lowercase (a-z)", true);
        chkNumbers = createStyledCheckbox("Numbers (0-9)", true);
        chkSymbols = createStyledCheckbox("Symbols (!@#$)", true);

        chkRow.add(chkUpper);
        chkRow.add(chkLower);
        chkRow.add(chkNumbers);
        chkRow.add(chkSymbols);
        controlsCardPanel.add(chkRow);

        mainContent.add(controlsCardPanel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 10)));

        // --------------------------------------------------------------------
        // SECTION 3: TABBED TOOLS (ZERO CLUTTER, ORGANIZED & CLEAN)
        // --------------------------------------------------------------------
        toolsTabbedPane = new JTabbedPane();
        toolsTabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        toolsTabbedPane.setForeground(Color.BLACK);
        toolsTabbedPane.setPreferredSize(new Dimension(700, 310));

        // TAB 1: QUICK PRESETS & ADVANCED
        JPanel tab1Presets = createPresetsTabPanel(t);
        toolsTabbedPane.addTab("  Quick 1-Click Presets  ", tab1Presets);

        // TAB 2: SAVED PASSWORDS VAULT
        JPanel tab2Vault = createVaultTabPanel(t);
        toolsTabbedPane.addTab("  Saved Vault (History)  ", tab2Vault);

        // TAB 3: BULK GENERATOR (5 to 50)
        JPanel tab3Bulk = createBulkTabPanel(t);
        toolsTabbedPane.addTab("  Bulk Generator (5-50)  ", tab3Bulk);

        mainContent.add(toolsTabbedPane);

        // ScrollPane Wrapper
        JScrollPane pageScrollPane = new JScrollPane(mainContent);
        pageScrollPane.setBorder(null);
        pageScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        dashboardRootPanel.add(pageScrollPane, BorderLayout.CENTER);

        // Initial password generation
        generatePassword();

        return dashboardRootPanel;
    }

    // ------------------------------------------------------------------------
    // TAB 1: 1-CLICK PRESETS PANEL
    // ------------------------------------------------------------------------
    private static JPanel createPresetsTabPanel(ThemePalette t) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(243, 232, 255)); // Soft Purple
        p.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel title = new JLabel("Click any profile to instantly configure and generate a password:");
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        title.setForeground(Color.BLACK);
        p.add(title);
        p.add(Box.createRigidArea(new Dimension(0, 10)));

        // 5 Preset Buttons
        JPanel grid = new JPanel(new GridLayout(1, 5, 8, 0));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JButton btnPin = createStyledButton("PIN (6 Digits)", new Color(254, 215, 170), new Color(251, 146, 60));
        btnPin.setToolTipText("Numeric PIN code for ATM, phone locks, or card verification");
        btnPin.addActionListener(e -> {
            modePinRadio.setSelected(true);
            applyPreset(6, false, false, true, false);
        });

        JButton btnWeb = createStyledButton("Web (14 Chars)", new Color(186, 230, 253), new Color(125, 211, 252));
        btnWeb.setToolTipText("Standard high-compatibility password for websites & email");
        btnWeb.addActionListener(e -> {
            modeRandomRadio.setSelected(true);
            applyPreset(14, true, true, true, false);
        });

        JButton btnStrong = createStyledButton("Strong (20 Chars)", new Color(187, 247, 208), new Color(134, 239, 172));
        btnStrong.setToolTipText("High-security password for banking, PayPal, and crypto wallets");
        btnStrong.addActionListener(e -> {
            modeRandomRadio.setSelected(true);
            applyPreset(20, true, true, true, true);
        });

        JButton btnUltra = createStyledButton("Ultra (32 Chars)", new Color(221, 214, 254), new Color(196, 181, 253));
        btnUltra.setToolTipText("Maximum-entropy password for password managers & root/admin keys");
        btnUltra.addActionListener(e -> {
            modeRandomRadio.setSelected(true);
            applyPreset(32, true, true, true, true);
        });

        JButton btnPassphrase = createStyledButton("Passphrase (4 Words)", new Color(254, 240, 138), new Color(250, 204, 21));
        btnPassphrase.setToolTipText("Diceware memorable multi-word passphrase with numeric suffix");
        btnPassphrase.addActionListener(e -> {
            modePassphraseRadio.setSelected(true);
            applyPreset(4, true, true, false, false);
        });

        grid.add(btnPin);
        grid.add(btnWeb);
        grid.add(btnStrong);
        grid.add(btnUltra);
        grid.add(btnPassphrase);
        p.add(grid);

        p.add(Box.createRigidArea(new Dimension(0, 14)));

        // Mode Radio selection
        JPanel modeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        modeRow.setOpaque(false);

        JLabel modeLbl = new JLabel("Engine Mode:");
        modeLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        modeLbl.setForeground(Color.BLACK);
        modeRow.add(modeLbl);

        modeRandomRadio = new JRadioButton("Random Characters", true);
        modePassphraseRadio = new JRadioButton("Diceware Words", false);
        modePinRadio = new JRadioButton("Numeric PIN", false);

        ButtonGroup grp = new ButtonGroup();
        for (JRadioButton rb : new JRadioButton[]{modeRandomRadio, modePassphraseRadio, modePinRadio}) {
            rb.setFont(new Font("Segoe UI", Font.BOLD, 12));
            rb.setForeground(Color.BLACK);
            rb.setOpaque(false);
            grp.add(rb);
            modeRow.add(rb);

            rb.addActionListener(ev -> {
                playClickSound();
                if (modePinRadio.isSelected()) {
                    lengthSlider.setValue(6);
                } else if (modePassphraseRadio.isSelected()) {
                    lengthSlider.setValue(4);
                } else {
                    lengthSlider.setValue(16);
                }
                generatePassword();
            });
        }
        p.add(modeRow);

        p.add(Box.createRigidArea(new Dimension(0, 10)));

        // Advanced Checkboxes
        JPanel advRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        advRow.setOpaque(false);

        chkAmbiguous = createStyledCheckbox("Exclude Ambiguous (il1Lo0O)", false);
        chkNoRepeat = createStyledCheckbox("No Consecutive Repeats", true);
        advRow.add(chkAmbiguous);
        advRow.add(chkNoRepeat);

        p.add(advRow);

        return p;
    }

    // ------------------------------------------------------------------------
    // TAB 2: SAVED VAULT PANEL
    // ------------------------------------------------------------------------
    private static JPanel createVaultTabPanel(ThemePalette t) {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBackground(new Color(238, 242, 255)); // Soft Indigo
        p.setBorder(new EmptyBorder(8, 10, 8, 10));

        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setOpaque(false);

        JPanel searchGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        searchGroup.setOpaque(false);

        JLabel searchIcon = new JLabel("Search Vault:");
        searchIcon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchIcon.setForeground(Color.BLACK);
        searchGroup.add(searchIcon);

        searchHistoryField = new JTextField(14);
        searchHistoryField.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchHistoryField.setForeground(Color.BLACK);
        searchHistoryField.setBackground(Color.WHITE);
        searchHistoryField.setBorder(new CompoundBorder(
                new LineBorder(Color.BLACK, 1, true),
                new EmptyBorder(3, 6, 3, 6)
        ));
        searchHistoryField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterHistoryList(); }
            public void removeUpdate(DocumentEvent e) { filterHistoryList(); }
            public void changedUpdate(DocumentEvent e) { filterHistoryList(); }
        });
        searchGroup.add(searchHistoryField);

        historyCountLabel = new JLabel("Total Saved: 0");
        historyCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        historyCountLabel.setForeground(Color.BLACK);
        searchGroup.add(historyCountLabel);

        top.add(searchGroup, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actions.setOpaque(false);

        JButton copySelectedBtn = createStyledButton("Copy Selected", new Color(187, 247, 208), new Color(134, 239, 172));
        copySelectedBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        copySelectedBtn.addActionListener(e -> {
            String selected = recentList.getSelectedValue();
            if (selected != null) {
                String clean = selected.contains(" | ") ? selected.split(" \\| ")[1].trim() : selected;
                copyToClipboard(clean);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Please click on a password in the list first!", "Notice", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        actions.add(copySelectedBtn);

        JButton exportBtn = createStyledButton("Export TXT File", new Color(221, 214, 254), new Color(196, 181, 253));
        exportBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        exportBtn.addActionListener(e -> exportRecentPasswords());
        actions.add(exportBtn);

        JButton clearBtn = createStyledButton("Clear History", new Color(254, 202, 202), new Color(248, 113, 113));
        clearBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        clearBtn.addActionListener(e -> {
            playClickSound();
            allHistoryModel.clear();
            filteredListModel.clear();
            historyCountLabel.setText("Total Saved: 0");
        });
        actions.add(clearBtn);

        top.add(actions, BorderLayout.EAST);
        p.add(top, BorderLayout.NORTH);

        allHistoryModel = new DefaultListModel<>();
        filteredListModel = new DefaultListModel<>();

        recentList = new JList<>(filteredListModel);
        recentList.setBackground(Color.WHITE);
        recentList.setForeground(Color.BLACK);
        recentList.setFont(new Font("Consolas", Font.BOLD, 14));
        recentList.setSelectionBackground(new Color(187, 247, 208));
        recentList.setSelectionForeground(Color.BLACK);

        recentList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    String selected = recentList.getSelectedValue();
                    if (selected != null) {
                        String clean = selected.contains(" | ") ? selected.split(" \\| ")[1].trim() : selected;
                        copyToClipboard(clean);
                    }
                }
            }
        });

        JScrollPane vaultScroll = new JScrollPane(recentList);
        vaultScroll.setBorder(new LineBorder(Color.BLACK, 1, true));
        p.add(vaultScroll, BorderLayout.CENTER);

        return p;
    }

    // ------------------------------------------------------------------------
    // TAB 3: BULK GENERATOR PANEL
    // ------------------------------------------------------------------------
    private static JPanel createBulkTabPanel(ThemePalette t) {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBackground(new Color(254, 243, 199)); // Amber
        p.setBorder(new EmptyBorder(8, 10, 8, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        top.setOpaque(false);

        JLabel countLbl = new JLabel("Batch Quantity:");
        countLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        countLbl.setForeground(Color.BLACK);
        top.add(countLbl);

        bulkSpinner = new JSpinner(new SpinnerNumberModel(10, 5, 50, 5));
        bulkSpinner.setFont(new Font("Segoe UI", Font.BOLD, 12));
        top.add(bulkSpinner);

        JButton runBtn = createStyledButton("Generate Bulk Passwords", new Color(52, 211, 153), new Color(16, 185, 129));
        runBtn.addActionListener(e -> {
            int qty = (Integer) bulkSpinner.getValue();
            StringBuilder sb = new StringBuilder();
            sb.append("=====================================================\n");
            sb.append("   PASSWORD PRO - BULK BATCH (Total: ").append(qty).append(" Passwords)\n");
            sb.append("=====================================================\n\n");
            for (int i = 1; i <= qty; i++) {
                generatePassword();
                sb.append(String.format("%02d. %s\n", i, rawCurrentPassword));
            }
            bulkTextArea.setText(sb.toString());
            bulkTextArea.setCaretPosition(0);
        });
        top.add(runBtn);

        JButton copyAllBtn = createStyledButton("Copy All Passwords", new Color(125, 211, 252), new Color(56, 189, 248));
        copyAllBtn.addActionListener(e -> copyToClipboard(bulkTextArea.getText()));
        top.add(copyAllBtn);

        p.add(top, BorderLayout.NORTH);

        bulkTextArea = new JTextArea();
        bulkTextArea.setFont(new Font("Consolas", Font.BOLD, 14));
        bulkTextArea.setForeground(Color.BLACK);
        bulkTextArea.setBackground(Color.WHITE);
        bulkTextArea.setEditable(false);
        bulkTextArea.setBorder(new EmptyBorder(8, 8, 8, 8));
        bulkTextArea.setText("Click 'Generate Bulk Passwords' above to create 5 to 50 passwords at once.");

        JScrollPane scroll = new JScrollPane(bulkTextArea);
        scroll.setBorder(new LineBorder(Color.BLACK, 1, true));
        p.add(scroll, BorderLayout.CENTER);

        return p;
    }

    // ========================================================================
    // THEME CYCLING ENGINE
    // ========================================================================
    private static void cycleTheme() {
        playClickSound();
        currentThemeIndex = (currentThemeIndex + 1) % THEMES.length;
        ThemePalette t = THEMES[currentThemeIndex];

        themeSwitchBtn.setText("Theme: " + t.name);

        if (dashboardRootPanel != null) {
            dashboardRootPanel.setBackground(t.appBg);
        }
        if (heroCardPanel != null) {
            heroCardPanel.setBackground(t.card1Bg);
            heroCardPanel.setBorder(new CompoundBorder(
                    new TitledBorder(
                            new LineBorder(t.card1Border, 3, true),
                            " YOUR GENERATED PASSWORD ",
                            TitledBorder.LEFT,
                            TitledBorder.TOP,
                            new Font("Segoe UI", Font.BOLD, 14),
                            Color.BLACK
                    ),
                    new EmptyBorder(8, 14, 12, 14)
            ));
        }
        if (controlsCardPanel != null) {
            controlsCardPanel.setBackground(t.card2Bg);
            controlsCardPanel.setBorder(new CompoundBorder(
                    new TitledBorder(
                            new LineBorder(t.card2Border, 3, true),
                            " CORE CONTROLS & RULES ",
                            TitledBorder.LEFT,
                            TitledBorder.TOP,
                            new Font("Segoe UI", Font.BOLD, 13),
                            Color.BLACK
                    ),
                    new EmptyBorder(8, 14, 10, 14)
            ));
            if (lengthSlider != null) {
                lengthSlider.setBackground(t.card2Bg);
            }
        }
        if (loginRootPanel != null) {
            loginRootPanel.setBackground(t.appBg);
        }
        if (loginCardPanel != null) {
            loginCardPanel.setBorder(new CompoundBorder(
                    new LineBorder(t.card1Border, 3, true),
                    new EmptyBorder(24, 35, 24, 35)
            ));
        }

        mainFrame.repaint();
    }

    private static void applyPreset(int length, boolean upper, boolean lower, boolean numbers, boolean symbols) {
        playClickSound();
        lengthSlider.setValue(length);
        chkUpper.setSelected(upper);
        chkLower.setSelected(lower);
        chkNumbers.setSelected(numbers);
        chkSymbols.setSelected(symbols);
        generatePassword();
    }

    private static void togglePasswordMask(JButton toggleBtn) {
        isPasswordMasked = !isPasswordMasked;
        if (isPasswordMasked) {
            toggleBtn.setText("Hide");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < rawCurrentPassword.length(); i++) sb.append("*");
            passwordDisplay.setText(sb.toString());
        } else {
            toggleBtn.setText("Show");
            passwordDisplay.setText(rawCurrentPassword);
        }
    }

    private static JCheckBox createStyledCheckbox(String text, boolean selected) {
        JCheckBox cb = new JCheckBox(text, selected);
        cb.setForeground(Color.BLACK); // 100% Black Text
        cb.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cb.setOpaque(false);
        cb.setFocusPainted(false);
        cb.addActionListener(e -> {
            playClickSound();
            generatePassword();
        });
        return cb;
    }

    private static JButton createStyledButton(String text, Color bg, Color hoverBg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.BLACK); // 100% Black Text
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(
                new LineBorder(Color.BLACK, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hoverBg);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    private static void playClickSound() {
        Toolkit.getDefaultToolkit().beep();
    }

    // ========================================================================
    // CORE PASSWORD ENGINE (CSPRNG SECURE RANDOM, PASSPHRASE, PIN)
    // ========================================================================
    private static void generatePassword() {
        SecureRandom random = new SecureRandom();
        String pass = "";

        if (modePassphraseRadio != null && modePassphraseRadio.isSelected()) {
            int words = lengthSlider != null ? Math.max(2, Math.min(8, lengthSlider.getValue())) : 4;
            List<String> chosen = new ArrayList<>();
            for (int i = 0; i < words; i++) {
                String w = WORD_DICTIONARY[random.nextInt(WORD_DICTIONARY.length)];
                w = Character.toUpperCase(w.charAt(0)) + w.substring(1);
                chosen.add(w);
            }
            pass = String.join("-", chosen) + "-" + (random.nextInt(90) + 10);

        } else if (modePinRadio != null && modePinRadio.isSelected()) {
            int len = lengthSlider != null ? Math.max(4, Math.min(16, lengthSlider.getValue())) : 6;
            StringBuilder sb = new StringBuilder();
            char last = ' ';
            for (int i = 0; i < len; i++) {
                char next;
                int attempts = 0;
                do {
                    next = (char) ('0' + random.nextInt(10));
                    attempts++;
                } while (chkNoRepeat != null && chkNoRepeat.isSelected() && next == last && attempts < 20);
                sb.append(next);
                last = next;
            }
            pass = sb.toString();

        } else {
            String lower = "abcdefghijklmnopqrstuvwxyz";
            String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            String numbers = "0123456789";
            String symbols = "!@#$%^&*()-_+=<>?";
            String ambiguous = "il1Lo0O|`'\"";

            if (chkAmbiguous != null && chkAmbiguous.isSelected()) {
                lower = filterChars(lower, ambiguous);
                upper = filterChars(upper, ambiguous);
                numbers = filterChars(numbers, ambiguous);
            }

            List<String> activeCategories = new ArrayList<>();

            if (chkUpper != null && chkUpper.isSelected()) {
                activeCategories.add(upper);
            }
            if (chkLower != null && chkLower.isSelected()) {
                activeCategories.add(lower);
            }
            if (chkNumbers != null && chkNumbers.isSelected()) {
                activeCategories.add(numbers);
            }
            if (chkSymbols != null && chkSymbols.isSelected()) {
                activeCategories.add(symbols);
            }

            if (activeCategories.isEmpty()) {
                if (chkLower != null) chkLower.setSelected(true);
                activeCategories.add(lower);
            }

            int length = lengthSlider != null ? lengthSlider.getValue() : 16;
            if (length < activeCategories.size()) {
                length = activeCategories.size();
                if (lengthSlider != null) lengthSlider.setValue(length);
            }

            StringBuilder compositePool = new StringBuilder();
            for (String cat : activeCategories) compositePool.append(cat);
            String allChars = compositePool.toString();

            List<Character> charList = new ArrayList<>(length);

            // Guarantee at least one character from each active category
            for (String cat : activeCategories) {
                charList.add(cat.charAt(random.nextInt(cat.length())));
            }

            // Fill remaining length
            int remaining = length - charList.size();
            for (int i = 0; i < remaining; i++) {
                charList.add(allChars.charAt(random.nextInt(allChars.length())));
            }

            // Cryptographic Fisher-Yates shuffle
            for (int i = charList.size() - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                Collections.swap(charList, i, j);
            }

            // No consecutive repeating characters
            if (chkNoRepeat != null && chkNoRepeat.isSelected() && charList.size() > 1) {
                for (int i = 1; i < charList.size(); i++) {
                    if (charList.get(i).equals(charList.get(i - 1))) {
                        for (int k = i + 1; k < charList.size(); k++) {
                            if (!charList.get(k).equals(charList.get(i - 1))) {
                                Collections.swap(charList, i, k);
                                break;
                            }
                        }
                    }
                }
            }

            StringBuilder passBuilder = new StringBuilder();
            for (char c : charList) passBuilder.append(c);
            pass = passBuilder.toString();
        }

        rawCurrentPassword = pass;
        if (isPasswordMasked) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pass.length(); i++) sb.append("*");
            passwordDisplay.setText(sb.toString());
        } else {
            passwordDisplay.setText(pass);
        }

        // Breakdown composition
        int uCount = 0, lCount = 0, dCount = 0, sCount = 0;
        for (char c : pass.toCharArray()) {
            if (Character.isUpperCase(c)) uCount++;
            else if (Character.isLowerCase(c)) lCount++;
            else if (Character.isDigit(c)) dCount++;
            else sCount++;
        }
        if (charBreakdownLabel != null) {
            charBreakdownLabel.setText(String.format("Composition: %d chars | Upper: %d, Lower: %d, Digits: %d, Symbols: %d",
                    pass.length(), uCount, lCount, dCount, sCount));
        }

        // Shannon Entropy calculation
        int poolSize = 0;
        if (uCount > 0) poolSize += 26;
        if (lCount > 0) poolSize += 26;
        if (dCount > 0) poolSize += 10;
        if (sCount > 0) poolSize += 32;
        if (poolSize == 0) poolSize = 1;

        double entropy = pass.length() * (Math.log(poolSize) / Math.log(2));

        // Crack-Time Estimation (GPU 100 Billion guesses/sec)
        double totalCombinations = Math.pow(2, Math.min(entropy, 120));
        double seconds = totalCombinations / 1e11;
        String crackStr;
        if (seconds < 1) crackStr = "< 1 sec";
        else if (seconds < 60) crackStr = String.format("%.0f Secs", seconds);
        else if (seconds < 3600) crackStr = String.format("%.0f Mins", seconds / 60);
        else if (seconds < 86400) crackStr = String.format("%.1f Hours", seconds / 3600);
        else if (seconds < 31536000) crackStr = String.format("%.0f Days", seconds / 86400);
        else if (seconds < 3.15e10) crackStr = String.format("%.0f Years", seconds / 31536000);
        else crackStr = "100+ Centuries";

        if (crackTimeLabel != null) {
            crackTimeLabel.setText("Crack Time: " + crackStr + " | Entropy: " + String.format("%.1f", entropy) + " Bits");
        }

        // Strength Rating
        if (pass.length() < 8 || entropy < 30) {
            strengthLabel.setText("Strength: VERY WEAK (" + String.format("%.0f", entropy) + " bits)");
            strengthLabel.setBackground(new Color(254, 202, 202));
            strengthProgressBar.setValue(20);
            strengthProgressBar.setForeground(new Color(239, 68, 68));
        } else if (pass.length() < 12 || entropy < 50) {
            strengthLabel.setText("Strength: WEAK (" + String.format("%.0f", entropy) + " bits)");
            strengthLabel.setBackground(new Color(254, 215, 170));
            strengthProgressBar.setValue(45);
            strengthProgressBar.setForeground(new Color(249, 115, 22));
        } else if (pass.length() < 16 || entropy < 75) {
            strengthLabel.setText("Strength: STRONG (" + String.format("%.0f", entropy) + " bits)");
            strengthLabel.setBackground(new Color(254, 240, 138));
            strengthProgressBar.setValue(75);
            strengthProgressBar.setForeground(new Color(234, 179, 8));
        } else {
            strengthLabel.setText("Strength: VERY STRONG (100%)");
            strengthLabel.setBackground(new Color(134, 239, 172));
            strengthProgressBar.setValue(100);
            strengthProgressBar.setForeground(new Color(34, 197, 94));
        }

        // Add to Recent Vault
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String entry = "[" + timeStamp + "] | " + pass;
        if (allHistoryModel != null) {
            allHistoryModel.add(0, entry);
            if (allHistoryModel.size() > 100) allHistoryModel.remove(allHistoryModel.size() - 1);
            filterHistoryList();
        }
    }

    private static String filterChars(String source, String removeChars) {
        StringBuilder sb = new StringBuilder();
        for (char c : source.toCharArray()) {
            if (removeChars.indexOf(c) == -1) sb.append(c);
        }
        return sb.toString();
    }

    private static void copyToClipboard(String text) {
        if (text == null || text.isEmpty()) return;
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(text), null);
        if (clipboardStatusBadge != null) {
            clipboardStatusBadge.setText("Copied: " + (text.length() > 20 ? text.substring(0, 20) + "..." : text));
            clipboardStatusBadge.setBackground(new Color(134, 239, 172));
        }
    }

    private static void filterHistoryList() {
        if (allHistoryModel == null || filteredListModel == null) return;
        String query = searchHistoryField != null ? searchHistoryField.getText().trim().toLowerCase() : "";
        filteredListModel.clear();
        for (int i = 0; i < allHistoryModel.size(); i++) {
            String item = allHistoryModel.get(i);
            if (query.isEmpty() || item.toLowerCase().contains(query)) {
                filteredListModel.addElement(item);
            }
        }
        if (historyCountLabel != null) {
            historyCountLabel.setText("Total Saved: " + allHistoryModel.size());
        }
    }

    // ========================================================================
    // MODAL: NATO PHONETIC SPELLER
    // ========================================================================
    private static void openNatoModal() {
        playClickSound();
        JDialog dialog = new JDialog(mainFrame, "NATO Phonetic Alphabet Speller", true);
        dialog.setSize(560, 380);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setLayout(new BorderLayout(8, 8));
        dialog.getContentPane().setBackground(new Color(241, 245, 249));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(254, 240, 138));
        top.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel title = new JLabel("Phonetic Spelling (Read aloud clearly during phone calls):");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(Color.BLACK);
        top.add(title, BorderLayout.WEST);

        JTextArea area = new JTextArea(decodeToNATO(rawCurrentPassword));
        area.setFont(new Font("Segoe UI", Font.BOLD, 14));
        area.setForeground(Color.BLACK);
        area.setBackground(Color.WHITE);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton copyNato = createStyledButton("Copy Spelling", new Color(125, 211, 252), new Color(56, 189, 248));
        copyNato.addActionListener(e -> copyToClipboard(area.getText()));
        top.add(copyNato, BorderLayout.EAST);

        dialog.add(top, BorderLayout.NORTH);
        dialog.add(new JScrollPane(area), BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private static String decodeToNATO(String pwd) {
        if (pwd == null || pwd.isEmpty()) return "No password currently generated!";
        Map<Character, String> map = new HashMap<>();
        map.put('A', "Alpha");   map.put('B', "Bravo");    map.put('C', "Charlie");
        map.put('D', "Delta");   map.put('E', "Echo");     map.put('F', "Foxtrot");
        map.put('G', "Golf");    map.put('H', "Hotel");    map.put('I', "India");
        map.put('J', "Juliett"); map.put('K', "Kilo");     map.put('L', "Lima");
        map.put('M', "Mike");    map.put('N', "November"); map.put('O', "Oscar");
        map.put('P', "Papa");    map.put('Q', "Quebec");   map.put('R', "Romeo");
        map.put('S', "Sierra");  map.put('T', "Tango");    map.put('U', "Uniform");
        map.put('V', "Victor");  map.put('W', "Whiskey");  map.put('X', "X-ray");
        map.put('Y', "Yankee");  map.put('Z', "Zulu");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pwd.length(); i++) {
            char c = pwd.charAt(i);
            String word = map.get(Character.toUpperCase(c));
            if (word != null) {
                if (Character.isLowerCase(c)) sb.append(word).append(" (small '").append(c).append("')");
                else sb.append(word).append(" (CAPITAL '").append(c).append("')");
            } else {
                sb.append("'").append(c).append("'");
            }
            if (i < pwd.length() - 1) sb.append("  |  ");
        }
        return sb.toString();
    }

    private static void exportRecentPasswords() {
        if (allHistoryModel == null || allHistoryModel.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Recent history vault is empty!", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("recent_passwords.txt"));
        if (chooser.showSaveDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (FileWriter fw = new FileWriter(file)) {
                fw.write("=====================================================\n");
                fw.write("   PASSWORD PRO - PASSWORD VAULT EXPORT\n");
                fw.write("   Export Timestamp: " + LocalDateTime.now() + "\n");
                fw.write("=====================================================\n\n");
                for (int i = 0; i < allHistoryModel.size(); i++) {
                    fw.write((i + 1) + ". " + allHistoryModel.get(i) + "\n");
                }
                JOptionPane.showMessageDialog(mainFrame, "Passwords exported successfully to:\n" + file.getAbsolutePath(), "Export Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(mainFrame, "Error saving file: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void saveCredentials(String username, String password) {
        Properties props = new Properties();
        props.setProperty("username", username);
        props.setProperty("password", password);
        try (FileOutputStream out = new FileOutputStream(CREDENTIALS_FILE)) {
            props.store(out, "Saved User Credentials for Password Pro");
        } catch (IOException e) {
            System.err.println("File save error: " + e.getMessage());
        }
    }

    private static void loadCredentials() {
        File file = new File(CREDENTIALS_FILE);
        if (file.exists()) {
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream(file)) {
                props.load(in);
                String user = props.getProperty("username", "");
                String pass = props.getProperty("password", "");
                if (!user.isEmpty()) {
                    userField.setText(user);
                    passField.setText(pass);
                    rememberMeBox.setSelected(true);
                }
            } catch (IOException e) {
                System.err.println("File load error: " + e.getMessage());
            }
        }
    }

    // Dynamic High-Resolution App Icon
    private static Image createAppIcon() {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(20, 20, 30));
        g.fillOval(4, 4, 56, 56);
        g.setColor(new Color(255, 30, 30));
        g.setStroke(new BasicStroke(3.5f));
        g.drawOval(7, 7, 50, 50);
        g.setColor(new Color(220, 20, 20));
        g.fillOval(18, 18, 28, 28);
        g.setColor(new Color(255, 180, 180));
        g.fillOval(24, 24, 16, 16);
        g.dispose();
        return img;
    }

    // ========================================================================
    // ROTATING RED CAR PROJECTOR HEADLIGHT VECTOR COMPONENT (ANIMATED CANVAS)
    // ========================================================================
    static class AnimatedCarEyeHeader extends JPanel {
        private double angle = 0;
        private final String text;
        private final Color textColor;
        private final int eyeCount;

        public AnimatedCarEyeHeader(String text, Color textColor, Color bgColor, int eyeCount) {
            this.text = text;
            this.textColor = textColor;
            this.eyeCount = eyeCount;
            setBackground(bgColor);

            javax.swing.Timer timer = new javax.swing.Timer(16, e -> {
                angle += 0.05;
                if (angle >= 2 * Math.PI) {
                    angle = 0;
                }
                repaint();
            });
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(text);
            int textX = (w - textW) / 2;
            int textY = (h - fm.getHeight()) / 2 + fm.getAscent();

            g2.setColor(textColor);
            g2.drawString(text, textX, textY);

            int eyeSize = 36;
            int centerY = h / 2 - eyeSize / 2;

            if (eyeCount == 2) {
                drawCarHeadlightEye(g2, textX - 48, centerY, eyeSize, angle);
                drawCarHeadlightEye(g2, textX + textW + 12, centerY, eyeSize, angle);
            } else {
                drawCarHeadlightEye(g2, textX - 92, centerY, eyeSize, angle);
                drawCarHeadlightEye(g2, textX - 48, centerY, eyeSize, angle);
                drawCarHeadlightEye(g2, textX + textW + 12, centerY, eyeSize, angle);
                drawCarHeadlightEye(g2, textX + textW + 56, centerY, eyeSize, angle);
            }

            g2.dispose();
        }

        private void drawCarHeadlightEye(Graphics2D g2, int x, int y, int size, double currentAngle) {
            Graphics2D g = (Graphics2D) g2.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Area clipArea = new Area(new Ellipse2D.Double(x, y, size, size));
            g.setClip(clipArea);

            double cx = x + size / 2.0;
            double cy = y + size / 2.0;

            AffineTransform at = new AffineTransform();
            at.translate(cx, cy);
            at.rotate(currentAngle);
            g.transform(at);

            double r = size / 2.0;

            // Base housing
            g.setColor(new Color(15, 15, 20));
            g.fill(new Ellipse2D.Double(-r, -r, size, size));

            // Outer Neon Halo Ring
            g.setColor(new Color(255, 30, 30));
            g.setStroke(new BasicStroke(3.0f));
            double outerHaloR = r * 0.85;
            g.draw(new Ellipse2D.Double(-outerHaloR, -outerHaloR, outerHaloR * 2, outerHaloR * 2));

            // Rotating LED projector dots
            int numDots = 10;
            double dotRingRadius = r * 0.62;
            g.setColor(new Color(255, 120, 120));
            for (int i = 0; i < numDots; i++) {
                double dotAngle = i * (2 * Math.PI / numDots);
                double dx = dotRingRadius * Math.cos(dotAngle);
                double dy = dotRingRadius * Math.sin(dotAngle);
                g.fill(new Ellipse2D.Double(dx - 2.0, dy - 2.0, 4.0, 4.0));
            }

            // Central Projector Lens
            double lensR = r * 0.42;
            g.setColor(new Color(200, 10, 10));
            g.fill(new Ellipse2D.Double(-lensR, -lensR, lensR * 2, lensR * 2));

            double bulbR = r * 0.18;
            g.setColor(new Color(255, 180, 180));
            g.fill(new Ellipse2D.Double(-bulbR, -bulbR, bulbR * 2, bulbR * 2));

            g.setClip(null);
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(1.8f));
            g.draw(new Ellipse2D.Double(-r, -r, size, size));

            g.dispose();
        }
    }
}
