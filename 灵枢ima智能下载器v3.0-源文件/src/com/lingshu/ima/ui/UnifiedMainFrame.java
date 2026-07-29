//版权声明
//本软件由作者:灵枢 开发，仅供学习交流使用
//严禁任何形式的非法破解、反编译、逆向工程等行为
//使用者应当遵守相关法律法规，尊重知识产权
//作者:灵枢 保留所有权利

package com.lingshu.ima.ui;

import com.lingshu.ima.config.AppConfig;
import com.lingshu.ima.dto.helper.KnowledgeItem;
import com.lingshu.ima.dto.helper.SearchKBInfo;
import com.lingshu.ima.dto.HomepageResponse;
import com.lingshu.ima.dto.SearchResponse;
import com.lingshu.ima.service.DownloadService;
import com.lingshu.ima.service.KnowledgeBrowserService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Proxy;
import java.util.prefs.Preferences;


public class UnifiedMainFrame extends JFrame {

    private final AppConfig config;
    private final DownloadService downloadService;
    private final KnowledgeBrowserService browserService;

    // === UI Color Theme (柔和现代配色) ===
    private static final Color PRIMARY = new Color(79, 140, 255);
    private static final Color PRIMARY_HOVER = new Color(59, 120, 240);
    private static final Color PRIMARY_LIGHT = new Color(227, 238, 255);
    private static final Color PRIMARY_SOFT = new Color(243, 248, 255);
    private static final Color DOWNLOAD_ORANGE = new Color(255, 140, 66);
    private static final Color DOWNLOAD_ORANGE_HOVER = new Color(245, 120, 40);
    private static final Color DOWNLOAD_ORANGE_LIGHT = new Color(255, 243, 230);
    private static final Color BATCH_GREEN = new Color(46, 204, 135);
    private static final Color BATCH_GREEN_HOVER = new Color(36, 180, 115);
    private static final Color BATCH_GREEN_LIGHT = new Color(225, 250, 240);
    private static final Color BROWSE_PURPLE = new Color(155, 110, 255);
    private static final Color BROWSE_PURPLE_HOVER = new Color(140, 90, 245);
    private static final Color BROWSE_PURPLE_LIGHT = new Color(245, 238, 255);
    private static final Color BG_DARK = new Color(38, 50, 70);
    private static final Color BG_CARD = new Color(255, 255, 255);
    private static final Color BG_PAGE = new Color(243, 246, 250);
    private static final Color TEXT_PRIMARY = new Color(33, 43, 60);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color TEXT_MUTED = new Color(160, 175, 192);
    private static final Color SUCCESS = new Color(52, 211, 153);
    private static final Color SUCCESS_HOVER = new Color(40, 185, 130);
    private static final Color DANGER = new Color(248, 90, 90);
    private static final Color DANGER_HOVER = new Color(230, 70, 70);
    private static final Color WARNING = new Color(255, 185, 50);
    private static final Color BORDER = new Color(230, 235, 242);
    private static final Color BORDER_LIGHT = new Color(242, 245, 250);
    private static final Color ACCENT_START = new Color(120, 100, 255);
    private static final Color ACCENT_END = new Color(79, 140, 255);
    private static final Color HEADER_START = new Color(88, 80, 236);
    private static final Color HEADER_END = new Color(56, 152, 255);
    private static final Color FOLDER_BG = new Color(255, 248, 230);
    private static final Color FOLDER_BORDER = new Color(255, 200, 60);
    private static final Color FILE_BG = new Color(235, 245, 255);
    private static final Color FILE_BORDER = new Color(100, 160, 255);
    // === File Type Color Scheme ===
    private static final Color ICON_PDF_BG = new Color(255, 230, 230);
    private static final Color ICON_PDF_BORDER = new Color(220, 60, 60);
    private static final Color ICON_WORD_BG = new Color(220, 235, 255);
    private static final Color ICON_WORD_BORDER = new Color(50, 120, 210);
    private static final Color ICON_EXCEL_BG = new Color(220, 245, 220);
    private static final Color ICON_EXCEL_BORDER = new Color(50, 160, 70);
    private static final Color ICON_PPT_BG = new Color(255, 230, 200);
    private static final Color ICON_PPT_BORDER = new Color(220, 120, 30);
    private static final Color ICON_IMG_BG = new Color(230, 220, 255);
    private static final Color ICON_IMG_BORDER = new Color(150, 80, 220);
    private static final Color ICON_VIDEO_BG = new Color(255, 215, 220);
    private static final Color ICON_VIDEO_BORDER = new Color(200, 50, 80);
    private static final Color ICON_AUDIO_BG = new Color(255, 240, 210);
    private static final Color ICON_AUDIO_BORDER = new Color(200, 150, 50);
    private static final Color ICON_ARCHIVE_BG = new Color(240, 230, 210);
    private static final Color ICON_ARCHIVE_BORDER = new Color(160, 120, 60);
    private static final Color ICON_CODE_BG = new Color(210, 240, 240);
    private static final Color ICON_CODE_BORDER = new Color(50, 160, 160);

    private Color[] getFileTypeColors(String ext, String typeName) {
        String e = ext.toLowerCase();
        if (e.matches("(pdf)") || "PDF".equals(typeName)) return new Color[]{ICON_PDF_BG, ICON_PDF_BORDER};
        if (e.matches("(doc|docx)") || typeName.contains("Word") || typeName.contains("WORD")) return new Color[]{ICON_WORD_BG, ICON_WORD_BORDER};
        if (e.matches("(xls|xlsx|csv)") || typeName.contains("Excel") || typeName.contains("EXCEL")) return new Color[]{ICON_EXCEL_BG, ICON_EXCEL_BORDER};
        if (e.matches("(ppt|pptx)") || typeName.contains("PPT") || typeName.contains("幻灯")) return new Color[]{ICON_PPT_BG, ICON_PPT_BORDER};
        if (e.matches("(jpg|jpeg|png|gif|bmp|webp|svg|tif|tiff)")) return new Color[]{ICON_IMG_BG, ICON_IMG_BORDER};
        if (e.matches("(mp4|avi|mkv|mov|wmv|flv|mpg|mpeg|webm)")) return new Color[]{ICON_VIDEO_BG, ICON_VIDEO_BORDER};
        if (e.matches("(mp3|wav|flac|aac|ogg|wma|m4a)")) return new Color[]{ICON_AUDIO_BG, ICON_AUDIO_BORDER};
        if (e.matches("(zip|rar|7z|tar|gz|bz2)")) return new Color[]{ICON_ARCHIVE_BG, ICON_ARCHIVE_BORDER};
        if (e.matches("(java|py|js|html|css|json|xml|sql|sh|bat|c|cpp|h|go|rs|ts|php|rb)")) return new Color[]{ICON_CODE_BG, ICON_CODE_BORDER};
        return new Color[]{FILE_BG, FILE_BORDER};
    }

    private static final Pattern HTML_TAG = Pattern.compile("<[^>]+>");
    private static final Pattern NON_ASCII_GARBAGE = Pattern.compile("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]");

    // === Tab 1: Discovery ===
    private JTextField searchField;
    private JButton searchButton;
    private JList<SearchKBInfo> searchResultList;
    private DefaultListModel<SearchKBInfo> searchResultModel;
    private JLabel statusLabel;
    private JTabbedPane mainTabbedPane;

    // === Tab 2: Download Manager (纯分享ID下载) ===
    private JTextArea inputTextArea;
    private JButton startDownloadButton;
    private JButton stopDownloadButton;
    private JButton openFolderButton;
    private JButton clearLogButton;
    private JButton pasteButton;
    private JCheckBox fastDownloadCheckBox;
    private JTable fileListTable;
    private DefaultTableModel fileListModel;
    private JTextArea logTextArea;
    private JScrollPane logScrollPane;
    private JLabel totalProgressLabel;
    private JProgressBar totalProgressBar;
    private JLabel currentFileLabel;
    private JProgressBar currentFileProgressBar;

    // === Tab 3: Browse (知识库浏览 - 独立浏览视图) ===
    private JList<KnowledgeItem> browseItemList;
    private DefaultListModel<KnowledgeItem> browseItemModel;
    private JLabel browseKBNameLabel;
    private JLabel browseStatusLabel;
    private JButton browseBackButton;
    private JButton browseDownloadAllButton;

    // === Tab 4: Batch Download (纯批量下载) ===
    private JList<KnowledgeItem> batchItemList;
    private DefaultListModel<KnowledgeItem> batchItemModel;
    private JButton batchStartButton;
    private JButton batchStopButton;
    private JButton batchClearButton;
    private JLabel batchStatusLabel;
    private JLabel batchKBNameLabel;
    private JProgressBar batchProgressBar;
    private JTextArea batchLogTextArea;
    private JScrollPane batchLogScrollPane;

    // === Per-item progress tracking ===
    private Map<String, Integer> itemProgressMap = new HashMap<>();
    private Map<String, String> itemStatusMap = new HashMap<>();

    // === State ===
    private int totalFiles = 0;
    private int successCount = 0;
    private int failedCount = 0;
    private int existingCount = 0;
    private List<String> originalShareIds = new ArrayList<>();
    private Map<String, String> shareIdToFolderName = new HashMap<>();
    private Map<String, String> currentFolderMap = new HashMap<>();
    private Map<String, String> fileNameToFolderId = new HashMap<>();
    private SearchKBInfo selectedKB;
    private List<KnowledgeItem> currentKBItems;
    private boolean isBatchDownloading = false;
    private boolean homepageLoaded = false;

    // === File Logging ===
    private static final String LOG_DIR = "logs";
    private BufferedWriter logWriter;
    private String currentLogFile;

    private void initFileLogger() {
        try {
            File logDir = new File(LOG_DIR);
            if (!logDir.exists()) logDir.mkdirs();
            String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            currentLogFile = LOG_DIR + File.separator + today + ".log";
            logWriter = new BufferedWriter(new FileWriter(currentLogFile, true));
            writeLogToFile("[系统] 灵枢IMA智能下载器 V3.0 启动");
        } catch (Exception e) {
            System.err.println("日志初始化失败: " + e.getMessage());
        }
    }

    private void writeLogToFile(String message) {
        try {
            if (logWriter != null) {
                String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
                logWriter.write("[" + time + "] " + message);
                logWriter.newLine();
                logWriter.flush();
            }
        } catch (Exception ignored) {}
    }

    private void closeFileLogger() {
        try {
            if (logWriter != null) {
                writeLogToFile("[系统] 程序关闭");
                logWriter.close();
                logWriter = null;
            }
        } catch (Exception ignored) {}
    }

    // === Favorites & Search History (using Java Preferences) ===
    private java.util.Set<String> favoriteKBIds = new java.util.HashSet<>();
    private java.util.List<String> searchHistory = new java.util.ArrayList<>();

    // === Download Speed / ETA ===
    private JLabel batchSpeedLabel;
    private JLabel batchEtaLabel;
    private long batchDownloadStartTime = 0;
    private final java.util.concurrent.atomic.AtomicLong batchDownloadedBytes = new java.util.concurrent.atomic.AtomicLong(0);

    // === Proxy Settings UI ===
    private JTextField proxyHostField;
    private JTextField proxyPortField;
    private JComboBox<String> proxyTypeCombo;


    public UnifiedMainFrame() {
        this.config = AppConfig.getInstance();
        this.downloadService = new DownloadService();
        this.browserService = new KnowledgeBrowserService();

        setTitle("灵枢IMA智能下载器 V3.0");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BG_PAGE);

        initComponents();
        createMenuBar();

        addLog("灵枢IMA智能下载器 V3.0 已启动");
        addLog("V3.0 就绪 - 尽情探索!");

        SwingUtilities.invokeLater(() -> showUsageGuide());
        loadFavorites();
        initFileLogger();
        loadSearchHistory();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                closeFileLogger();
            }
        });
    }

    // ======================== 使用说明弹窗 ========================

    private void showUsageGuide() {
        String css = "font-family:'Microsoft YaHei',sans-serif; font-size:12.5px; color:#1e293b; padding:10px 14px; line-height:1.6;";
        String h2 = "font-size:15px; color:#3b82f6; text-align:center; margin:8px 0 4px;";
        String h3 = "font-size:12.5px; color:#3b82f6; margin:10px 0 2px; border-bottom:1px solid #e2e8f0; padding-bottom:2px;";
        String li = "margin:1px 0; font-size:12px;";
        String b = "color:#1e40af;";
        String htmlContent = "<html><body style='" + css + "'>"
            + "<div style='" + h2 + "'><b>灵枢IMA智能下载器 V3.0</b></div>"
            + "<hr style='border:1px solid #cbd5e1; margin:4px 0 8px;'>"
            + "<div style='" + h3 + "'><b>快速开始</b></div>"
            + "<ul style='" + li + "'>"
            + "<li>双击 <b style='" + b + "'>start.bat</b> 启动程序</li>"
            + "<li>搜索框输入关键词按回车搜索，或浏览首页推荐</li>"
            + "<li><b style='" + b + "'>双击</b>知识库卡片浏览文件，点击开始批量下载</li>"
            + "</ul>"
            + "<div style='" + h3 + "'><b>四大功能标签</b></div>"
            + "<ul style='" + li + "'>"
            + "<li><b style='" + b + "'>发现页</b> - 搜索知识库 + 首页推荐 + 收藏夹 + 搜索历史</li>"
            + "<li><b style='" + b + "'>浏览</b> - 文件列表详情（大小/类型/更新时间）+ 10层子文件夹 + 文件预览</li>"
            + "<li><b style='" + b + "'>下载管理</b> - 粘贴知识库ID或分享链接 + 实时速度/剩余时间 + 断点续传 + 失败重试</li>"
            + "<li><b style='" + b + "'>批量下载</b> - 从浏览页点击下载全部 + 单项进度 + 总进度</li>"
            + "</ul>"
            + "<div style='" + h3 + "'><b>操作流程</b></div>"
            + "<ul style='" + li + "'>"
            + "<li>方式一（推荐）：发现页搜索 → 双击知识库 → 浏览文件 → 批量下载</li>"
            + "<li>方式二：发现页搜索 → 右键知识库 → 批量下载</li>"
            + "<li>方式三：下载管理 → 粘贴知识库ID（纯数字） → 开始下载（自动获取原名）</li>"
            + "<li>方式四：下载管理 → 粘贴分享链接 → 开始下载</li>"
            + "</ul>"
            + "<div style='" + h3 + "'><b>V3.0 新增功能</b></div>"
            + "<ul style='" + li + "'>"
            + "<li>下载速度显示 + 剩余时间估算</li>"
            + "<li>下载队列管理 + 失败重试 + 断点续传</li>"
            + "<li>知识库收藏夹 + 搜索历史记录</li>"
            + "<li>文件预览 + 导出下载清单 + 代理设置</li>"
            + "</ul>"
            + "<div style='" + h3 + "'><b>注意事项</b></div>"
            + "<ul style='" + li + "'>"
            + "<li>不采集个人信息，所有功能<b style='" + b + "'>无需登录</b></li>"
            + "<li>仅供学习交流，下载内容版权归原作者所有</li>"
            + "<li>未经授权禁止反编译、修改、分发或商业使用</li>"
            + "</ul>"
            + "<div style='font-size:10px; color:#94a3b8; text-align:center; margin:8px 0 2px; border-top:1px solid #e2e8f0; padding-top:4px;'>Copyright 2026 灵枢 版权所有</div>"
            + "</body></html>";

        JEditorPane editorPane = new JEditorPane("text/html", htmlContent);
        editorPane.setEditable(false);
        editorPane.setPreferredSize(new Dimension(520, 520));
        editorPane.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        JOptionPane.showMessageDialog(this, scrollPane,
            "灵枢IMA智能下载器 V3.0 - 使用说明", JOptionPane.INFORMATION_MESSAGE);
    }

    private void initComponents() {
        mainTabbedPane = new JTabbedPane(JTabbedPane.TOP);
        mainTabbedPane.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        mainTabbedPane.setBackground(BG_PAGE);
        mainTabbedPane.setOpaque(true);
        mainTabbedPane.setForeground(TEXT_SECONDARY);
        mainTabbedPane.setBorder(new EmptyBorder(0, 0, 0, 0));

        mainTabbedPane.addTab("    发现  ", createDiscoveryTab());
        mainTabbedPane.addTab("    浏览  ", createBrowseTab());
        mainTabbedPane.addTab("    下载管理  ", createDownloadTab());
        mainTabbedPane.addTab("    批量下载  ", createBatchTab());

        mainTabbedPane.addChangeListener(e -> {
            int idx = mainTabbedPane.getSelectedIndex();
            if (idx == 0 && !homepageLoaded) loadHomepage();
            switch (idx) {
                case 0: applyTabStyle(0, PRIMARY, PRIMARY_LIGHT); break;
                case 1: applyTabStyle(1, BROWSE_PURPLE, BROWSE_PURPLE_LIGHT); break;
                case 2: applyTabStyle(2, DOWNLOAD_ORANGE, DOWNLOAD_ORANGE_LIGHT); break;
                case 3: applyTabStyle(3, BATCH_GREEN, BATCH_GREEN_LIGHT); break;
            }
            // Fix image residual: force full repaint when switching tabs
            final java.awt.Component selected = mainTabbedPane.getSelectedComponent();
            if (selected != null) {
                SwingUtilities.invokeLater(() -> {
                    selected.revalidate();
                    selected.repaint();
                    // Also repaint all JScrollPane viewports recursively
                    repaintDeep(selected);
                });
            }
        });

        applyTabStyle(0, PRIMARY, PRIMARY_LIGHT);

        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBackground(BG_PAGE);
        mainPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel headerPanel = createGradientHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(mainTabbedPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void applyTabStyle(int index, Color color, Color lightColor) {
        for (int i = 0; i < mainTabbedPane.getTabCount(); i++) {
            mainTabbedPane.setForegroundAt(i, i == index ? color : TEXT_MUTED);
        }
    }

    private JPanel createGradientHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, HEADER_START, getWidth(), 0, HEADER_END);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 0, 0);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        header.setBackground(PRIMARY);
        header.setPreferredSize(new Dimension(0, 50));
        header.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel titleLabel = new JLabel("  灵枢IMA智能下载器");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel, BorderLayout.WEST);

        JLabel enjoyLabel = new JLabel("尽情探索!");
        enjoyLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        enjoyLabel.setForeground(new Color(210, 225, 255, 200));
        header.add(enjoyLabel, BorderLayout.CENTER);

        JLabel versionLabel = new JLabel("V3.0  ");
        versionLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 11));
        versionLabel.setForeground(new Color(200, 218, 255));
        header.add(versionLabel, BorderLayout.EAST);

        return header;
    }


    // Force deep repaint to clear any cached image artifacts
    private void repaintDeep(java.awt.Component c) {
        if (c instanceof javax.swing.JComponent) {
            javax.swing.JComponent jc = (javax.swing.JComponent) c;
            jc.setDoubleBuffered(true);
            jc.revalidate();
            jc.repaint();
            if (jc instanceof JScrollPane) {
                JScrollPane sp = (JScrollPane) jc;
                sp.getViewport().revalidate();
                sp.getViewport().repaint();
            }
        }
        if (c instanceof java.awt.Container) {
            for (java.awt.Component child : ((java.awt.Container) c).getComponents()) {
                repaintDeep(child);
            }
        }
    }
    // ======================== Tab 1: Discovery ========================

    private JPanel createDiscoveryTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(BG_PAGE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Search bar - 改进圆角搜索框
        JPanel searchPanel = new JPanel(new BorderLayout(8, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BORDER_LIGHT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 21, 21);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        searchPanel.setLayout(new BorderLayout(8, 0));
        searchPanel.setBorder(new EmptyBorder(6, 14, 6, 10));
        searchPanel.setBackground(Color.WHITE);

        searchField = new JTextField();
        searchField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        searchField.setBorder(null);
        searchField.setBackground(new Color(0, 0, 0, 0));
        searchField.setOpaque(false);
        searchField.setToolTipText("输入关键词后按回车搜索");
        searchField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { searchField.repaint(); searchPanel.repaint(); }
            @Override public void focusLost(FocusEvent e) { searchField.repaint(); searchPanel.repaint(); }
        });
        searchField.addActionListener(e -> doSearch());

        JPanel searchBtnWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        searchBtnWrapper.setOpaque(false);
        searchButton = createRoundedButton("搜索", PRIMARY, PRIMARY_HOVER, 90, 36);
        searchButton.addActionListener(e -> doSearch());
        searchBtnWrapper.add(searchButton);

        JPanel searchExtraBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        searchExtraBtns.setOpaque(false);
        JButton refreshBtn = createRoundedButton("刷新", PRIMARY, PRIMARY_HOVER, 65, 36);
        refreshBtn.addActionListener(e -> { homepageLoaded = false; loadHomepage(); });
        JButton historyBtn = createRoundedButton("历史", TEXT_MUTED, BG_DARK, 65, 36);
        historyBtn.addActionListener(e -> showSearchHistoryDialog());
        JButton favBtn = createRoundedButton("收藏", TEXT_MUTED, BG_DARK, 65, 36);
        favBtn.addActionListener(e -> showFavoritesDialog());
        searchExtraBtns.add(refreshBtn);
        searchExtraBtns.add(historyBtn);
        searchExtraBtns.add(favBtn);

        JPanel searchCenterWrapper = new JPanel(new BorderLayout(4, 0));
        searchCenterWrapper.setOpaque(false);
        searchCenterWrapper.add(searchField, BorderLayout.CENTER);
        searchCenterWrapper.add(searchExtraBtns, BorderLayout.EAST);
        searchPanel.add(searchCenterWrapper, BorderLayout.CENTER);
        searchPanel.add(searchBtnWrapper, BorderLayout.EAST);

        // Status bar
        // Status bar with opaque background to prevent image bleed-through
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(BG_PAGE);
        statusBar.setOpaque(true);
        statusLabel = new JLabel("  正在加载首页推荐...");
        statusLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        statusLabel.setForeground(TEXT_SECONDARY);
        statusLabel.setBorder(new EmptyBorder(4, 4, 4, 0));
        statusBar.add(statusLabel, BorderLayout.CENTER);

        // Result list - 滑动残留修复: 统一白色背景 + 强制opaque
        searchResultModel = new DefaultListModel<>();
        searchResultList = new JList<>(searchResultModel);
        searchResultList.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        searchResultList.setCellRenderer(new SearchKBCellRenderer());
        searchResultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchResultList.setFixedCellHeight(88);
        searchResultList.setBorder(new EmptyBorder(4, 4, 4, 4));
        searchResultList.setBackground(Color.WHITE);
        searchResultList.setOpaque(true);


        searchResultList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) doBrowseKB();
            }
            @Override
            public void mousePressed(MouseEvent e) { showPopup(e); }
            @Override
            public void mouseReleased(MouseEvent e) { showPopup(e); }
        });

        // 滑动残留修复: SIMPLE scroll mode + viewport背景
        JScrollPane scrollPane = new JScrollPane(searchResultList);
        scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setOpaque(true);
        scrollPane.setDoubleBuffered(true);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1, true),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        JPanel centerPanel = new JPanel(new BorderLayout(0, 6));
        centerPanel.setBackground(BG_PAGE);
        centerPanel.setDoubleBuffered(true);
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(statusBar, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);
        SwingUtilities.invokeLater(this::loadHomepage);
        return panel;
    }

    private void showPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            int idx = searchResultList.locationToIndex(e.getPoint());
            if (idx >= 0) {
                searchResultList.setSelectedIndex(idx);
                JPopupMenu popup = createKBPopupMenu();
                popup.show(searchResultList, e.getX(), e.getY());
            }
        }
    }

    private JPopupMenu createKBPopupMenu() {
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(BORDER, 1));

        JMenuItem browseItem = new JMenuItem("浏览知识库");
        browseItem.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        browseItem.addActionListener(ev -> doBrowseKB());
        JMenuItem downloadItem = new JMenuItem("批量下载");
        downloadItem.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        downloadItem.addActionListener(ev -> doBatchDownloadKB());
        JMenuItem copyIdItem = new JMenuItem("复制知识库ID");
        copyIdItem.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        copyIdItem.addActionListener(ev -> {
            SearchKBInfo kb = searchResultList.getSelectedValue();
            if (kb != null && kb.getKnowledgeBaseId() != null) {
                java.awt.datatransfer.StringSelection sel = new java.awt.datatransfer.StringSelection(kb.getKnowledgeBaseId());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);
                addLog("已复制知识库ID: " + kb.getKnowledgeBaseId());
            }
        });
        popup.add(browseItem);
        popup.add(downloadItem);
        popup.addSeparator();        JMenuItem favToggleItem = new JMenuItem("收藏/取消收藏");
        favToggleItem.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        favToggleItem.addActionListener(ev -> {
            SearchKBInfo kbFav = searchResultList.getSelectedValue();
            if (kbFav != null) toggleFavorite(kbFav.getKnowledgeBaseId(), cleanText(kbFav.getTitle()));
        });
        popup.add(favToggleItem);
        popup.add(copyIdItem);
        return popup;
    }

    private void loadHomepage() {
        statusLabel.setText("  正在加载推荐...");
        browserService.loadHomepageAsync(new KnowledgeBrowserService.BrowseCallback() {
            @Override public void onSearchResult(List<SearchKBInfo> results, int totalCount) {}
            @Override
            public void onHomepageResult(HomepageResponse response) {
                SwingUtilities.invokeLater(() -> {
                    searchResultModel.clear();
                    List<SearchKBInfo> allItems = new ArrayList<>();
                    if (response.getRecommendList() != null) allItems.addAll(response.getRecommendList());
                    if (response.getHotList() != null) allItems.addAll(response.getHotList());
                    Map<String, SearchKBInfo> seen = new HashMap<>();
                    for (SearchKBInfo item : allItems) {
                        if (item.getKnowledgeBaseId() != null && !seen.containsKey(item.getKnowledgeBaseId())) {
                            seen.put(item.getKnowledgeBaseId(), item);
                            searchResultModel.addElement(item);
                        }
                    }
                    homepageLoaded = true;
                    statusLabel.setText("  已加载 " + searchResultModel.size() + " 个知识库 | 双击浏览 | 右键更多操作");
                    addLog("[OK] 发现页加载完成! " + searchResultModel.size() + " 个知识库");
                });
            }
            @Override public void onBrowseFolder(List<KnowledgeItem> items, String folderName) {}
            @Override
            public void onError(String message) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("  加载失败: " + message);
                    addLog("首页加载错误: " + message);
                });
            }
            @Override public void onLoading(boolean loading) {}
        });
    }

    private void doSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            homepageLoaded = false;
            loadHomepage();
            return;
        }
        statusLabel.setText("  搜索中: " + query + "...");
        searchButton.setEnabled(false);
        browserService.searchAsync(query, new KnowledgeBrowserService.BrowseCallback() {
            @Override
            public void onSearchResult(List<SearchKBInfo> results, int totalCount) {
                SwingUtilities.invokeLater(() -> {
                    searchResultModel.clear();
                    if (results != null) {
                        for (SearchKBInfo item : results) searchResultModel.addElement(item);
                    }
                    statusLabel.setText("  找到 " + totalCount + " 个结果, 显示 " + searchResultModel.size() + " 个");
                    searchButton.setEnabled(true);
                    addLog("搜索 \"" + query + "\" 完成! " + totalCount + " 个结果");
                    addToSearchHistory(query);
                });
            }
            @Override public void onHomepageResult(HomepageResponse response) {}
            @Override public void onBrowseFolder(List<KnowledgeItem> items, String folderName) {}
            @Override
            public void onError(String message) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("  搜索失败: " + message);
                    searchButton.setEnabled(true);
                    addLog("搜索错误: " + message);
                });
            }
            @Override public void onLoading(boolean loading) {
                if (!loading) SwingUtilities.invokeLater(() -> searchButton.setEnabled(true));
            }
        });
    }

    private void doBrowseKB() {
        SearchKBInfo kb = searchResultList.getSelectedValue();
        if (kb == null || kb.getKnowledgeBaseId() == null) return;
        selectedKB = kb;
        String kbName = cleanText(kb.getTitle());
        statusLabel.setText("  正在浏览: " + kbName + "...");
        addLog("浏览知识库: " + kbName + " (ID: " + kb.getKnowledgeBaseId() + ")");

        browserService.browseKnowledgeBaseAsync(kb.getKnowledgeBaseId(), kbName,
            new KnowledgeBrowserService.BrowseCallback() {
                @Override public void onSearchResult(List<SearchKBInfo> results, int totalCount) {}
                @Override public void onHomepageResult(HomepageResponse response) {}
                @Override
                public void onBrowseFolder(List<KnowledgeItem> items, String folderName) {
                    SwingUtilities.invokeLater(() -> {
                        currentKBItems = items;
                        mainTabbedPane.setSelectedIndex(1);
                        browseItemModel.clear();
                        itemProgressMap.clear();
                        itemStatusMap.clear();
                        int fileCount = 0, folderCount = 0;
                        if (items != null) {
                            for (KnowledgeItem item : items) {
                                browseItemModel.addElement(item);
                                if (item.getMediaId() != null && item.getMediaId().startsWith("folder_")) {
                                    folderCount++;
                                } else {
                                    fileCount++;
                                    String mid = item.getMediaId();
                                    if (mid != null) { itemProgressMap.put(mid, 0); itemStatusMap.put(mid, "就绪"); }
                                }
                            }
                        }
                        browseKBNameLabel.setText(folderName);
                        browseDownloadAllButton.setEnabled(fileCount > 0);
                        browseStatusLabel.setText("  " + fileCount + " 个文件, " + folderCount + " 个文件夹 | 双击文件夹浏览");
                        browseItemList.repaint();
                        addLog(" 浏览完成: " + folderName + " - " + items.size() + " 个项目");
                    });
                }
                @Override
                public void onError(String message) {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("  浏览失败: " + message);
                        addLog("浏览错误: " + message);
                    });
                }
                @Override public void onLoading(boolean loading) {}
            });
    }

    private void doBatchDownloadKB() {
        SearchKBInfo kb = searchResultList.getSelectedValue();
        if (kb == null || kb.getKnowledgeBaseId() == null) return;
        selectedKB = kb;
        String kbName = cleanText(kb.getTitle());
        int confirm = JOptionPane.showConfirmDialog(this,
            "确认批量下载 \"" + kbName + "\"?\n文件将保存到下载目录。",
            "确认", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        mainTabbedPane.setSelectedIndex(3);
        startBatchDownload(kb.getKnowledgeBaseId(), kbName);
    }

    // ======================== Tab 2: Browse ========================

    private JPanel createBrowseTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(BG_PAGE);
        panel.setDoubleBuffered(true);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel controlCard = createCardPanel();
        controlCard.setLayout(new BorderLayout(8, 8));
        controlCard.setBorder(new EmptyBorder(10, 12, 10, 12));

        browseKBNameLabel = new JLabel("在发现页双击知识库开始浏览");
        browseKBNameLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        browseKBNameLabel.setForeground(BROWSE_PURPLE);

        browseStatusLabel = new JLabel("  等待浏览...");
        browseStatusLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        browseStatusLabel.setForeground(TEXT_SECONDARY);

        JPanel browseBtnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        browseBtnRow.setBackground(Color.WHITE);

        browseBackButton = createRoundedButton("返回发现", TEXT_MUTED, BG_DARK, 110, 34);
        browseBackButton.addActionListener(e -> mainTabbedPane.setSelectedIndex(0));

        browseDownloadAllButton = createRoundedButton("下载全部", BATCH_GREEN, BATCH_GREEN_HOVER, 120, 34);
        browseDownloadAllButton.setEnabled(false);
        browseDownloadAllButton.addActionListener(e -> {
            if (selectedKB != null && !isBatchDownloading) {
                mainTabbedPane.setSelectedIndex(3);
                startBatchDownload(selectedKB.getKnowledgeBaseId(), cleanText(selectedKB.getTitle()));
            }
        });

        browseBtnRow.add(browseBackButton);
        browseBtnRow.add(browseDownloadAllButton);

        JPanel labelPanel = new JPanel(new BorderLayout(0, 2));
        labelPanel.setBackground(Color.WHITE);
        labelPanel.add(browseKBNameLabel, BorderLayout.NORTH);
        labelPanel.add(browseStatusLabel, BorderLayout.CENTER);

        controlCard.add(labelPanel, BorderLayout.CENTER);
        controlCard.add(browseBtnRow, BorderLayout.SOUTH);

        browseItemModel = new DefaultListModel<>();
        browseItemList = new JList<>();
        browseItemList.setModel(browseItemModel);
        browseItemList.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        browseItemList.setCellRenderer(new BrowseItemCellRenderer());
        browseItemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        browseItemList.setFixedCellHeight(62);
        browseItemList.setBackground(Color.WHITE);
        browseItemList.setOpaque(true);

        browseItemList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int idx = browseItemList.locationToIndex(e.getPoint());
                    if (idx < 0) return;
                    KnowledgeItem item = browseItemModel.getElementAt(idx);
                    if (item == null) return;
                    if (item.getMediaId() != null && item.getMediaId().startsWith("folder_")) {
                        doBrowseSubFolder(item);
                    } else {
                        showFileDetailDialog(item);
                    }
                }
            }
            @Override
            public void mousePressed(MouseEvent e) { showBrowsePopup(e); }
            @Override
            public void mouseReleased(MouseEvent e) { showBrowsePopup(e); }
        });

        JScrollPane browseScroll = new JScrollPane(browseItemList);
        browseScroll.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        browseScroll.getViewport().setBackground(Color.WHITE);
        browseScroll.getViewport().setOpaque(true);
        browseScroll.setBackground(Color.WHITE);
        browseScroll.setOpaque(true);
        browseScroll.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1, true));

        panel.add(controlCard, BorderLayout.NORTH);
        panel.add(browseScroll, BorderLayout.CENTER);
        return panel;
    }

    private void showFileDetailDialog(KnowledgeItem item) {
        String name = cleanText(item.getTitle());
        String type = item.getMediaTypeInfo() != null ? item.getMediaTypeInfo().getName() : "未知";
        String path = item.getSourcePath() != null ? item.getSourcePath() : "无路径";
        String size = item.getFileSize() != null ? formatFileSize(Long.parseLong(item.getFileSize().replaceAll("[^0-9]", ""))) : "未知";
        String updateTime = item.getUpdateTime() != null ? formatUpdateTime(item.getUpdateTime()) : "未知";
        String hasDownload = item.getJumpUrl() != null ? "[有下载链接]" : "[无下载链接]";

        String html = "<html><body style='font-family: Microsoft YaHei; font-size: 13px; padding: 12px; color: #212b3c;'>"
            + "<h3 style='color: #9b6eff; margin-top: 0;'>" + name + "</h3>"
            + "<hr style='border: 1px solid #e6ebf2;'>"
            + "<table style='width: 100%;'>"
            + "<tr><td style='color: #64748b; width: 80px;'>类型</td><td><b>" + type + "</b></td></tr>"
            + "<tr><td style='color: #64748b;'>[大小]</td><td><b>" + size + "</b></td></tr>"
            + "<tr><td style='color: #64748b;'>[路径]</td><td>" + path + "</td></tr>"
            + "<tr><td style='color: #64748b;'>[更新]</td><td>" + updateTime + "</td></tr>"
            + "<tr><td style='color: #64748b;'>[下载]</td><td>" + hasDownload + "</td></tr>"
            + "</table></body></html>";

        JEditorPane editor = new JEditorPane("text/html", html);
        editor.setEditable(false);
        editor.setPreferredSize(new Dimension(420, 220));
        editor.setBackground(Color.WHITE);
        JOptionPane.showMessageDialog(this, editor, "文件详情", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showBrowsePopup(MouseEvent e) {
        if (!e.isPopupTrigger()) return;
        int idx = browseItemList.locationToIndex(e.getPoint());
        if (idx < 0) return;
        browseItemList.setSelectedIndex(idx);
        KnowledgeItem item = browseItemModel.getElementAt(idx);
        if (item == null) return;

        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        String name = cleanText(item.getTitle() != null ? item.getTitle() : item.getMediaId());

        if (item.getMediaId() != null && item.getMediaId().startsWith("folder_")) {
            JMenuItem browseItem = new JMenuItem("浏览: " + name);
            browseItem.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
            browseItem.addActionListener(ev -> doBrowseSubFolder(item));
            popup.add(browseItem);
        } else {
            JMenuItem infoItem = new JMenuItem("详情: " + name);
            infoItem.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
            infoItem.addActionListener(ev -> showFileDetailDialog(item));
            popup.add(infoItem);
            JMenuItem previewItem = new JMenuItem("预览: " + name);
            previewItem.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
            previewItem.addActionListener(ev -> previewFile(item));
            popup.add(previewItem);
        }

        JMenuItem dlAllItem = new JMenuItem("下载当前列表全部文件");
        dlAllItem.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        dlAllItem.addActionListener(ev -> {
            if (selectedKB != null && !isBatchDownloading) {
                mainTabbedPane.setSelectedIndex(3);
                startBatchDownload(selectedKB.getKnowledgeBaseId(), cleanText(selectedKB.getTitle()));
            }
        });
        popup.addSeparator();
        popup.add(dlAllItem);
        popup.show(browseItemList, e.getX(), e.getY());
    }

    private void doBrowseSubFolder(KnowledgeItem folderItem) {
        if (selectedKB == null) {
            addBatchLog("[错误] 未选择知识库");
            return;
        }
        String folderId = folderItem.getMediaId();
        if (folderId == null || folderId.isEmpty()) {
            addBatchLog("[错误] 文件夹ID为空，无法浏览");
            browseStatusLabel.setText("  [错误] 文件夹ID为空，无法浏览");
            return;
        }
        String folderName = folderItem.getSourcePath() != null ? folderItem.getSourcePath() : folderItem.getTitle();
        if (folderName == null || folderName.isEmpty()) folderName = "子文件夹";
        browseStatusLabel.setText("  正在浏览子文件夹: " + folderName + "...");

        browserService.browseFolderAsync(selectedKB.getKnowledgeBaseId(), folderId, folderName,
            new KnowledgeBrowserService.BrowseCallback() {
                @Override public void onSearchResult(List<SearchKBInfo> results, int totalCount) {}
                @Override public void onHomepageResult(HomepageResponse response) {}
                @Override
                public void onBrowseFolder(List<KnowledgeItem> items, String name) {
                    SwingUtilities.invokeLater(() -> {
                        currentKBItems = items;
                        browseItemModel.clear();
                        itemProgressMap.clear();
                        itemStatusMap.clear();
                        int fileCount = 0, folderCount = 0;
                        if (items != null) {
                            for (KnowledgeItem item : items) {
                                browseItemModel.addElement(item);
                                if (item.getMediaId() != null && item.getMediaId().startsWith("folder_")) {
                                    folderCount++;
                                } else {
                                    fileCount++;
                                    String mid = item.getMediaId();
                                    if (mid != null) { itemProgressMap.put(mid, 0); itemStatusMap.put(mid, "就绪"); }
                                }
                            }
                        }
                        browseDownloadAllButton.setEnabled(fileCount > 0);
                        browseKBNameLabel.setText(name);
                        browseStatusLabel.setText("  " + fileCount + " 个文件, " + folderCount + " 个文件夹 | 双击浏览");
                        browseItemList.repaint();
                        addLog("子文件夹: " + name + " - " + items.size() + " 个项目");
                    });
                }
                @Override
                public void onError(String message) {
                    SwingUtilities.invokeLater(() -> {
                        browseStatusLabel.setText("  浏览失败: " + message);
                        addLog("[错误] 浏览子文件夹失败: " + message);
                    });
                }
                @Override public void onLoading(boolean loading) {}
            });
    }

    // ======================== Tab 3: Download Manager ========================

    private JPanel createDownloadTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(BG_PAGE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topCard = createCardPanel();
        topCard.setLayout(new BorderLayout(8, 8));
        topCard.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel inputLabel = new JLabel("分享ID / 分享链接 (每行一个): ");
        inputLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        inputLabel.setForeground(DOWNLOAD_ORANGE);

        inputTextArea = new JTextArea(4, 30);
        inputTextArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        JScrollPane inputScroll = new JScrollPane(inputTextArea);
        inputScroll.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        buttonRow.setBackground(Color.WHITE);

        pasteButton = createRoundedButton("粘贴", TEXT_MUTED, BG_DARK, 80, 34);
        pasteButton.addActionListener(e -> pasteFromClipboard());
        startDownloadButton = createRoundedButton("开始下载", DOWNLOAD_ORANGE, DOWNLOAD_ORANGE_HOVER, 120, 34);
        startDownloadButton.addActionListener(e -> startShareIdDownload());
        stopDownloadButton = createRoundedButton("[X] 停止", DANGER, DANGER_HOVER, 80, 34);
        stopDownloadButton.setEnabled(false);
        stopDownloadButton.addActionListener(e -> stopDownload());
        openFolderButton = createRoundedButton("打开文件夹", TEXT_MUTED, BG_DARK, 110, 34);
        openFolderButton.addActionListener(e -> openDownloadFolder());
        clearLogButton = createRoundedButton("清空日志", TEXT_MUTED, BG_DARK, 100, 34);
        clearLogButton.addActionListener(e -> logTextArea.setText(""));
        fastDownloadCheckBox = new JCheckBox("极速模式");
        fastDownloadCheckBox.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        fastDownloadCheckBox.setForeground(TEXT_PRIMARY);
        fastDownloadCheckBox.setSelected(config.isFastDownloadMode());

        buttonRow.add(pasteButton);
        buttonRow.add(startDownloadButton);
        buttonRow.add(stopDownloadButton);
        buttonRow.add(openFolderButton);
        buttonRow.add(clearLogButton);
        buttonRow.add(fastDownloadCheckBox);

        JPanel inputArea = new JPanel(new BorderLayout(0, 8));
        inputArea.setBackground(Color.WHITE);
        inputArea.add(inputLabel, BorderLayout.NORTH);
        inputArea.add(inputScroll, BorderLayout.CENTER);
        inputArea.add(buttonRow, BorderLayout.SOUTH);
        topCard.add(inputArea, BorderLayout.CENTER);

        JPanel progressCard = createCardPanel();
        progressCard.setLayout(new GridLayout(2, 2, 10, 6));
        progressCard.setBorder(new EmptyBorder(10, 12, 10, 12));

        totalProgressLabel = new JLabel("总计: 就绪");
        totalProgressLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        totalProgressLabel.setForeground(TEXT_SECONDARY);
        totalProgressBar = createColoredProgressBar(DOWNLOAD_ORANGE, DOWNLOAD_ORANGE_HOVER);
        currentFileLabel = new JLabel("当前: --");
        currentFileLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        currentFileLabel.setForeground(TEXT_SECONDARY);
        currentFileProgressBar = createColoredProgressBar(DOWNLOAD_ORANGE, DOWNLOAD_ORANGE_HOVER);

        progressCard.add(totalProgressLabel);
        progressCard.add(totalProgressBar);
        progressCard.add(currentFileLabel);
        progressCard.add(currentFileProgressBar);

        fileListModel = new DefaultTableModel(new String[]{"#", "类型", "文件名", "大小", "状态"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int columnIndex) { return String.class; }
        };
        fileListTable = new JTable(fileListModel);
        fileListTable.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        fileListTable.setRowHeight(28);
        fileListTable.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 11));
        fileListTable.getTableHeader().setBackground(DOWNLOAD_ORANGE_LIGHT);
        fileListTable.getTableHeader().setForeground(DOWNLOAD_ORANGE);
        fileListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileListTable.setGridColor(BORDER_LIGHT);
        fileListTable.getColumnModel().getColumn(0).setPreferredWidth(35);
        fileListTable.getColumnModel().getColumn(0).setMaxWidth(45);
        fileListTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        fileListTable.getColumnModel().getColumn(1).setMaxWidth(65);
        fileListTable.getColumnModel().getColumn(3).setPreferredWidth(70);
        fileListTable.getColumnModel().getColumn(3).setMaxWidth(80);
        fileListTable.getColumnModel().getColumn(4).setPreferredWidth(65);
        fileListTable.getColumnModel().getColumn(4).setMaxWidth(75);
        fileListTable.getColumnModel().getColumn(2).setPreferredWidth(300);

        JScrollPane tableScroll = new JScrollPane(fileListTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1, true));
        tableScroll.setPreferredSize(new Dimension(0, 180));

        logTextArea = new JTextArea(6, 30);
        logTextArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        logTextArea.setEditable(false);
        logTextArea.setLineWrap(true);
        logScrollPane = new JScrollPane(logTextArea);
        logScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "[下载日志]",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Microsoft YaHei", Font.BOLD, 11), DOWNLOAD_ORANGE),
            new EmptyBorder(4, 4, 4, 4)
        ));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, logScrollPane);
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(null);

        JPanel bodyPanel = new JPanel(new BorderLayout(0, 8));
        bodyPanel.setBackground(BG_PAGE);
        bodyPanel.add(topCard, BorderLayout.NORTH);
        bodyPanel.add(progressCard, BorderLayout.CENTER);
        bodyPanel.add(splitPane, BorderLayout.SOUTH);
        panel.add(bodyPanel, BorderLayout.CENTER);
        return panel;
    }

    private void startShareIdDownload() {
        String inputText = inputTextArea.getText().trim();
        if (inputText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入分享ID或分享链接", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] lines = inputText.split("\\n");
        List<String> shareIds = new ArrayList<>();
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            if (line.matches("^\\d{10,25}$")) {
                // Knowledge base ID - use KB download mode
                String kbId = line;
                String kbTitle = null;
                try {
                    com.lingshu.ima.dto.SearchResponse searchResp = downloadService.getApiService().searchKnowledgeBase(kbId, 0, 10);
                    if (searchResp.getKnowledgeBaseList() != null) {
                        for (com.lingshu.ima.dto.helper.SearchKBInfo info : searchResp.getKnowledgeBaseList()) {
                            if (kbId.equals(info.getKnowledgeBaseId())) {
                                kbTitle = info.getTitle();
                                break;
                            }
                        }
                    }
                } catch (Exception ex) {
                    addLog("[Warn] 查询知识库名称失败: " + ex.getMessage());
                }
                if (kbTitle == null || kbTitle.isEmpty()) kbTitle = "知识库";
                addLog("KB ID: " + kbId + " -> " + kbTitle + ", downloading in this tab...");
                doKbIdDownload(kbId, kbTitle);
                return;
            }
            String shareId = downloadService.getApiService().extractShareId(line);
            if (shareId != null) shareIds.add(shareId);
            else shareIds.add(line);
        }

        if (shareIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "未找到有效的分享ID或链接", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        originalShareIds = shareIds;
        totalFiles = 0; successCount = 0; failedCount = 0; existingCount = 0;
        startDownloadButton.setEnabled(false);
        stopDownloadButton.setEnabled(true);
        fileListModel.setRowCount(0);
        addLog("正在处理 " + shareIds.size() + " 个分享ID...");

        downloadService.downloadAll(shareIds, new DownloadService.DownloadProgressCallback() {
            @Override public void onProgress(DownloadService.DownloadProgress progress) {
                SwingUtilities.invokeLater(() -> updateProgress(progress));
            }
            @Override public void onComplete(int total, int success, int failed, int existing, long duration) {
                SwingUtilities.invokeLater(() -> {
                    String t = formatDuration(duration);
                    addLog("下载完成! 总计: " + total + " | 成功: " + success + " | 失败: " + failed + " | 已存在: " + existing + " | " + t);
                    totalProgressLabel.setText("完成! 成功: " + success + " | 失败: " + failed + " | 已存在: " + existing);
                    startDownloadButton.setEnabled(true);
                    stopDownloadButton.setEnabled(false);
                });
            }
            @Override public void onError(String message) {
                SwingUtilities.invokeLater(() -> {
                    addLog("[Error] " + message);
                    startDownloadButton.setEnabled(true);
                    stopDownloadButton.setEnabled(false);
                });
            }
        });
    }


    // Download by KB ID in Download Manager tab (uses tab 2 UI)
    private void doKbIdDownload(String knowledgeBaseId, String title) {
        if (downloadService.isRunning()) {
            JOptionPane.showMessageDialog(this, "已有下载任务进行中", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        startDownloadButton.setEnabled(false);
        stopDownloadButton.setEnabled(true);
        fileListModel.setRowCount(0);
        totalProgressLabel.setText("总计: 获取文件列表中...");
        addLog("开始获取知识库文件列表: " + knowledgeBaseId);

        downloadService.downloadByKnowledgeBaseId(knowledgeBaseId, title, new DownloadService.DownloadProgressCallback() {
            @Override public void onProgress(DownloadService.DownloadProgress progress) {
                SwingUtilities.invokeLater(() -> updateProgress(progress));
            }
            @Override public void onComplete(int total, int success, int failed, int existing, long duration) {
                SwingUtilities.invokeLater(() -> {
                    String t = formatDuration(duration);
                    addLog("KB下载完成! 总计: " + total + " | 成功: " + success + " | 失败: " + failed + " | 已存在: " + existing + " | " + t);
                    totalProgressLabel.setText("完成! 成功: " + success + " | 失败: " + failed + " | 已存在: " + existing);
                    startDownloadButton.setEnabled(true);
                    stopDownloadButton.setEnabled(false);
                });
            }
            @Override public void onError(String message) {
                SwingUtilities.invokeLater(() -> {
                    addLog("[Error] " + message);
                    startDownloadButton.setEnabled(true);
                    stopDownloadButton.setEnabled(false);
                });
            }
        });
    }

    private void stopDownload() {
        if (downloadService.isRunning()) {
            downloadService.stop();
            addLog("用户停止了下载");
            startDownloadButton.setEnabled(true);
            stopDownloadButton.setEnabled(false);
        }
    }

    // ======================== Tab 4: Batch Download ========================

    private JPanel createBatchTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(BG_PAGE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel controlCard = createCardPanel();
        controlCard.setLayout(new BorderLayout(8, 8));
        controlCard.setBorder(new EmptyBorder(10, 12, 10, 12));

        batchStatusLabel = new JLabel("  从浏览Tab点击「下载全部」开始批量下载");
        batchStatusLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        batchStatusLabel.setForeground(BATCH_GREEN);

        batchKBNameLabel = new JLabel("");
        batchKBNameLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        batchKBNameLabel.setForeground(TEXT_PRIMARY);

        JPanel batchBtnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        batchBtnRow.setBackground(Color.WHITE);

        batchStartButton = createRoundedButton("开始批量下载", BATCH_GREEN, BATCH_GREEN_HOVER, 150, 34);
        batchStartButton.setEnabled(false);
        batchStartButton.addActionListener(e -> {
            if (selectedKB != null) startBatchDownload(selectedKB.getKnowledgeBaseId(), cleanText(selectedKB.getTitle()));
        });
        batchStopButton = createRoundedButton("[X] 停止", DANGER, DANGER_HOVER, 80, 34);
        batchStopButton.setEnabled(false);
        batchStopButton.addActionListener(e -> stopBatchDownload());
        batchClearButton = createRoundedButton("清空", TEXT_MUTED, BG_DARK, 80, 34);
        batchClearButton.addActionListener(e -> {
            batchItemModel.clear();
            batchLogTextArea.setText("");
            itemProgressMap.clear();
            itemStatusMap.clear();
            batchStatusLabel.setText("  已清空, 继续探索!");
        });

        batchBtnRow.add(batchStartButton);
        batchBtnRow.add(batchStopButton);
        batchBtnRow.add(batchClearButton);        batchSpeedLabel = new JLabel("速度: --");
        batchSpeedLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        batchSpeedLabel.setForeground(BATCH_GREEN);
        batchEtaLabel = new JLabel("剩余: --");
        batchEtaLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        batchEtaLabel.setForeground(TEXT_SECONDARY);

        batchBtnRow.add(batchSpeedLabel);
        batchBtnRow.add(batchEtaLabel);


        JPanel labelPanel = new JPanel(new BorderLayout(0, 2));
        labelPanel.setBackground(Color.WHITE);
        labelPanel.add(batchStatusLabel, BorderLayout.NORTH);
        labelPanel.add(batchKBNameLabel, BorderLayout.CENTER);

        controlCard.add(labelPanel, BorderLayout.CENTER);
        controlCard.add(batchBtnRow, BorderLayout.SOUTH);

        batchItemModel = new DefaultListModel<>();
        batchItemList = new JList<>();
        batchItemList.setModel(batchItemModel);
        batchItemList.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        batchItemList.setCellRenderer(new BatchItemCellRenderer());
        batchItemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        batchItemList.setFixedCellHeight(66);
        batchItemList.setBackground(Color.WHITE);
        batchItemList.setOpaque(true);

        batchItemList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int idx = batchItemList.locationToIndex(e.getPoint());
                    if (idx < 0) return;
                    KnowledgeItem item = batchItemModel.getElementAt(idx);
                    if (item == null) return;
                    if (item.getMediaId() != null && item.getMediaId().startsWith("folder_")) {
                        doBrowseSubFolder(item);
                    } else {
                        showFileDetailDialog(item);
                    }
                }
            }
            @Override
            public void mousePressed(MouseEvent e) { showBatchPopup(e); }
            @Override
            public void mouseReleased(MouseEvent e) { showBatchPopup(e); }
        });

        JScrollPane batchScroll = new JScrollPane(batchItemList);
        batchScroll.getViewport().setBackground(Color.WHITE);
        batchScroll.getViewport().setOpaque(true);
        batchScroll.setBackground(Color.WHITE);
        batchScroll.setOpaque(true);
        batchScroll.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1, true));

        batchProgressBar = createColoredProgressBar(BATCH_GREEN, BATCH_GREEN_HOVER);

        batchLogTextArea = new JTextArea(5, 30);
        batchLogTextArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        batchLogTextArea.setEditable(false);
        batchLogTextArea.setLineWrap(true);
        batchLogScrollPane = new JScrollPane(batchLogTextArea);
        batchLogScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "[下载日志]",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Microsoft YaHei", Font.BOLD, 11), BATCH_GREEN),
            new EmptyBorder(4, 4, 4, 4)
        ));

        JPanel bottomPanel = new JPanel(new BorderLayout(0, 6));
        bottomPanel.setBackground(BG_PAGE);
        bottomPanel.add(batchProgressBar, BorderLayout.NORTH);
        bottomPanel.add(batchLogScrollPane, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, batchScroll, bottomPanel);
        splitPane.setResizeWeight(0.7);
        splitPane.setBorder(null);

        panel.add(controlCard, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    // ======================== Download Logic ========================

    private void startBatchDownload(String knowledgeBaseId, String title) {
        if (isBatchDownloading || downloadService.isRunning()) {
            JOptionPane.showMessageDialog(this, "已有下载任务进行中", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        isBatchDownloading = true;
        batchStartButton.setEnabled(false);
        batchStopButton.setEnabled(true);
        batchProgressBar.setValue(0);
        batchProgressBar.setString("准备中...");
        batchLogTextArea.setText("");
        batchKBNameLabel.setText(title);
        batchSpeedLabel.setText("速度: --");
        batchEtaLabel.setText("剩余: --");
        batchDownloadStartTime = System.currentTimeMillis();
        batchDownloadedBytes.set(0L);
        addBatchLog(">> 开始下载! " + title + " (ID: " + knowledgeBaseId + ")");
        itemProgressMap.clear();
        itemStatusMap.clear();
        batchItemModel.clear();

        // Populate batchItemList with current items for visual tracking
        if (currentKBItems != null) {
            for (KnowledgeItem item : currentKBItems) {
                String mid = item.getMediaId();
                if (mid != null && !mid.startsWith("folder_")) {
                    itemProgressMap.put(mid, 0);
                    itemStatusMap.put(mid, "等待中");
                    batchItemModel.addElement(item);
                }
            }
        }
        addBatchLog("批量下载列表: " + batchItemModel.size() + " 个文件");

        downloadService.downloadByKnowledgeBaseId(knowledgeBaseId, title, new DownloadService.DownloadProgressCallback() {
            private int processedCount = 0;
            private int totalItemCount = 0;

            @Override public void onProgress(DownloadService.DownloadProgress progress) {
                SwingUtilities.invokeLater(() -> {
                    if (progress.getFileName() != null && !progress.getFileName().isEmpty()) {
                        if (progress.getFileName().contains(" 个文件")) {
                            totalItemCount = Integer.parseInt(progress.getFileName().replaceAll("[^0-9]", ""));
                            batchProgressBar.setMaximum(totalItemCount);
                            addBatchLog("[总计] " + progress.getFileName());
                        } else {
                            String status = progress.getStatus();
                            if ("已完成".equals(status) || "已存在".equals(status) || (status != null && status.startsWith("下载失败"))) {
                                processedCount++;
                                int pv = (int) ((double) processedCount / Math.max(1, totalItemCount) * 100);
                                batchProgressBar.setValue(pv);
                                batchProgressBar.setString(processedCount + "/" + totalItemCount + " (" + pv + "%)");
                                addBatchLog("[" + status + "] " + progress.getFileName());
                            }
                            if ("下载中".equals(status)) {
                                // Update per-item progress during download
                                String fn = progress.getFileName();
                                if (fn != null) {
                                    for (int bi = 0; bi < batchItemModel.size(); bi++) {
                                        KnowledgeItem bItem = batchItemModel.getElementAt(bi);
                                        String bName = cleanText(bItem.getTitle());
                                        if (bName != null && (bName.equals(fn) || fn.contains(bName.substring(0, Math.min(bName.length(), 20))))) {
                                            itemProgressMap.put(bItem.getMediaId(), progress.getProgress());
                                            itemStatusMap.put(bItem.getMediaId(), "下载中 " + progress.getProgress() + "%");
                                            break;
                                        }
                                    }
                                }
                                batchDownloadedBytes.addAndGet(progress.getDownloadedBytes());
                            }
                            if ("已完成".equals(status)) {
                                String fn = progress.getFileName();
                                if (fn != null) {
                                    for (int bi = 0; bi < batchItemModel.size(); bi++) {
                                        KnowledgeItem bItem = batchItemModel.getElementAt(bi);
                                        String bName = cleanText(bItem.getTitle());
                                        if (bName != null && (bName.equals(fn) || fn.contains(bName.substring(0, Math.min(bName.length(), 20))))) {
                                            itemProgressMap.put(bItem.getMediaId(), 100);
                                            itemStatusMap.put(bItem.getMediaId(), "已完成");
                                            break;
                                        }
                                    }
                                }
                                batchDownloadedBytes.addAndGet(progress.getFileSize());
                            }
                            if ("已存在".equals(status)) {
                                String fn = progress.getFileName();
                                if (fn != null) {
                                    for (int bi = 0; bi < batchItemModel.size(); bi++) {
                                        KnowledgeItem bItem = batchItemModel.getElementAt(bi);
                                        String bName = cleanText(bItem.getTitle());
                                        if (bName != null && (bName.equals(fn) || fn.contains(bName.substring(0, Math.min(bName.length(), 20))))) {
                                            itemProgressMap.put(bItem.getMediaId(), 100);
                                            itemStatusMap.put(bItem.getMediaId(), "已存在");
                                            break;
                                        }
                                    }
                                }
                            }
                            batchItemList.repaint();
                            if (batchDownloadStartTime > 0) {
                                long elapsed = System.currentTimeMillis() - batchDownloadStartTime;
                                if (elapsed > 1000) {
                                    double speedBps = (double) batchDownloadedBytes.get() / (elapsed / 1000.0);
                                    // Prevent negative or zero speed display
                                    if (speedBps < 0) speedBps = 0;
                                    if (speedBps > 1024 * 1024) {
                                        batchSpeedLabel.setText(String.format("速度: %.1f MB/s", speedBps / 1024.0 / 1024.0));
                                    } else if (speedBps > 1024) {
                                        batchSpeedLabel.setText(String.format("速度: %.1f KB/s", speedBps / 1024.0));
                                    } else {
                                        batchSpeedLabel.setText(String.format("速度: %.0f B/s", speedBps));
                                    }
                                    int remaining = totalItemCount - processedCount;
                                    if (remaining > 0 && processedCount > 0) {
                                        long avgTimePerItem = elapsed / processedCount;
                                        long etaSec = remaining * avgTimePerItem / 1000;
                                        batchEtaLabel.setText("剩余: " + formatDuration(etaSec * 1000));
                                    } else {
                                        batchEtaLabel.setText("剩余: 计算中...");
                                    }
                                }
                            }
                        }
                    }
                });
            }
            @Override public void onComplete(int total, int success, int failed, int existing, long duration) {
                SwingUtilities.invokeLater(() -> {
                    String t = formatDuration(duration);
                    addBatchLog("=== 下载完成! === 总计: " + total + " | 成功: " + success + " | 失败: " + failed + " | 已存在: " + existing + " | " + t);
                    batchProgressBar.setValue(batchProgressBar.getMaximum());
                    batchProgressBar.setString("完成! " + success + "/" + total);
                    batchStatusLabel.setText("  [完成] 下载完成: " + title + " | 成功: " + success + " | 失败: " + failed);
                    batchSpeedLabel.setText("速度: 完成");
                    batchEtaLabel.setText("剩余: 0s");
                    batchStartButton.setEnabled(true);
                    batchStopButton.setEnabled(false);
                    isBatchDownloading = false;
                    batchItemList.repaint();
                });
            }
            @Override public void onError(String message) {
                SwingUtilities.invokeLater(() -> {
                    addBatchLog("[Error] " + message);
                    batchStartButton.setEnabled(true);
                    batchStopButton.setEnabled(false);
                    isBatchDownloading = false;
                });
            }
        });
    }

    private void stopBatchDownload() {
        if (downloadService.isRunning()) {
            downloadService.stop();
            addBatchLog("用户停止了下载");
            batchStartButton.setEnabled(true);
            batchStopButton.setEnabled(false);
            isBatchDownloading = false;
        }
    }

    private void showBatchPopup(MouseEvent e) {
        if (!e.isPopupTrigger()) return;
        int idx = batchItemList.locationToIndex(e.getPoint());
        if (idx < 0) return;
        batchItemList.setSelectedIndex(idx);
        KnowledgeItem item = batchItemModel.getElementAt(idx);
        if (item == null) return;

        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        String name = cleanText(item.getTitle() != null ? item.getTitle() : item.getMediaId());

        if (item.getMediaId() != null && item.getMediaId().startsWith("folder_")) {
            JMenuItem browseItem = new JMenuItem("浏览: " + name);
            browseItem.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
            browseItem.addActionListener(ev -> doBrowseSubFolder(item));
            popup.add(browseItem);
        } else {
            JMenuItem infoItem = new JMenuItem("详情: " + name);
            infoItem.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
            infoItem.addActionListener(ev -> showFileDetailDialog(item));
            popup.add(infoItem);
        }

        JMenuItem previewItem2 = new JMenuItem("预览: " + name);
        previewItem2.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        previewItem2.addActionListener(ev -> previewFile(item));
        popup.add(previewItem2);

        JMenuItem retryItem = new JMenuItem("重试失败项");
        retryItem.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        retryItem.addActionListener(ev -> retryFailedDownloads());
        popup.add(retryItem);

        JMenuItem dlAllItem = new JMenuItem("下载当前列表全部文件");
        dlAllItem.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        dlAllItem.addActionListener(ev -> {
            if (selectedKB != null && !isBatchDownloading) {
                startBatchDownload(selectedKB.getKnowledgeBaseId(), cleanText(selectedKB.getTitle()));
            }
        });
        popup.addSeparator();
        popup.add(dlAllItem);
        popup.show(batchItemList, e.getX(), e.getY());
    }

    private void updateProgress(DownloadService.DownloadProgress progress) {
        if (progress.getFileName() != null && progress.getFileName().contains(" 个文件")) {
            totalFiles = Integer.parseInt(progress.getFileName().replaceAll("[^0-9]", ""));
            totalProgressBar.setMaximum(totalFiles);
            fileListModel.setRowCount(0);
            totalProgressLabel.setText("总计: 0/" + totalFiles);
            return;
        }

        if (progress.getFileName() != null && !progress.getFileName().isEmpty()) {
            String status = progress.getStatus();
            if ("已完成".equals(status)) successCount++;
            else if ("已存在".equals(status)) existingCount++;
            else if (status != null && status.startsWith("下载失败")) failedCount++;

            int processed = successCount + failedCount + existingCount;
            int pv = (int) ((double) processed / Math.max(1, totalFiles) * 100);
            totalProgressBar.setValue(pv);
            totalProgressBar.setString(pv + "%");

            fileListModel.addRow(new Object[]{
                processed, progress.getFileType(), progress.getFileName(),
                formatFileSize(progress.getFileSize()), status
            });
            fileListTable.scrollRectToVisible(fileListTable.getCellRect(fileListModel.getRowCount() - 1, 0, true));
            totalProgressLabel.setText("进度: " + processed + "/" + totalFiles +
                " | 成功: " + successCount + " | 失败: " + failedCount + " | 已存在: " + existingCount);
        }
    }


    // ======================== Favorites Management ========================

    private void loadFavorites() {
        try {
            Preferences prefs = Preferences.userNodeForPackage(UnifiedMainFrame.class);
            String favStr = prefs.get("favorites", "");
            favoriteKBIds.clear();
            if (!favStr.isEmpty()) {
                for (String id : favStr.split("\\|")) {
                    if (!id.trim().isEmpty()) favoriteKBIds.add(id.trim());
                }
            }
            addLog("已加载 " + favoriteKBIds.size() + " 个收藏");
        } catch (Exception e) {
            addLog("加载收藏失败: " + e.getMessage());
        }
    }

    private void saveFavorites() {
        try {
            Preferences prefs = Preferences.userNodeForPackage(UnifiedMainFrame.class);
            prefs.put("favorites", String.join("|", favoriteKBIds));
        } catch (Exception e) {
            addLog("保存收藏失败: " + e.getMessage());
        }
    }

    private void toggleFavorite(String kbId, String kbName) {
        if (kbId == null) return;
        if (favoriteKBIds.contains(kbId)) {
            favoriteKBIds.remove(kbId);
            addLog("已取消收藏: " + kbName);
        } else {
            favoriteKBIds.add(kbId);
            addLog("已收藏: " + kbName);
        }
        saveFavorites();
    }

    private void showFavoritesDialog() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setPreferredSize(new Dimension(480, 360));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("收藏夹 (" + favoriteKBIds.size() + " 个)");
        title.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        title.setForeground(PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        DefaultListModel<String> favModel = new DefaultListModel<>();
        for (String id : favoriteKBIds) favModel.addElement(id);
        JList<String> favList = new JList<>(favModel);
        favList.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        favList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(favList), BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        JButton browseFavBtn = createRoundedButton("浏览选中", BROWSE_PURPLE, BROWSE_PURPLE_HOVER, 100, 32);
        JButton removeFavBtn = createRoundedButton("移除选中", DANGER, DANGER_HOVER, 100, 32);
        JButton closeBtn = createRoundedButton("关闭", TEXT_MUTED, BG_DARK, 80, 32);
        btnRow.add(browseFavBtn);
        btnRow.add(removeFavBtn);
        btnRow.add(closeBtn);
        panel.add(btnRow, BorderLayout.SOUTH);

        browseFavBtn.addActionListener(ev -> {
            String sel = favList.getSelectedValue();
            if (sel != null) {
                browserService.browseKnowledgeBaseAsync(sel, sel,
                    new KnowledgeBrowserService.BrowseCallback() {
                        @Override public void onSearchResult(List<SearchKBInfo> r, int t) {}
                        @Override public void onHomepageResult(HomepageResponse r) {}
                        @Override public void onBrowseFolder(List<KnowledgeItem> items, String n) {
                            SwingUtilities.invokeLater(() -> {
                                currentKBItems = items;
                                mainTabbedPane.setSelectedIndex(1);
                                browseItemModel.clear();
                                itemProgressMap.clear();
                                itemStatusMap.clear();
                                if (items != null) {
                                    int fc = 0;
                                    for (KnowledgeItem fi : items) {
                                        browseItemModel.addElement(fi);
                                        String fm = fi.getMediaId();
                                        if (fm != null && !fm.startsWith("folder_")) {
                                            fc++;
                                            itemProgressMap.put(fm, 0);
                                            itemStatusMap.put(fm, "就绪");
                                        }
                                    }
                                    browseDownloadAllButton.setEnabled(fc > 0);
                                }
                                browseKBNameLabel.setText(n);
                                browseStatusLabel.setText("  " + items.size() + " 个项目");
                            });
                        }
                        @Override public void onError(String msg) { addLog("浏览收藏失败: " + msg); }
                        @Override public void onLoading(boolean l) {}
                    });
            }
        });
        removeFavBtn.addActionListener(ev -> {
            String sel = favList.getSelectedValue();
            if (sel != null) {
                favoriteKBIds.remove(sel);
                saveFavorites();
                favModel.removeElement(sel);
                title.setText("收藏夹 (" + favoriteKBIds.size() + " 个)");
                addLog("已移除收藏: " + sel);
            }
        });
        final JDialog[] favDialog = new JDialog[1];
        favDialog[0] = new JDialog(this, "收藏夹管理", false);
        closeBtn.addActionListener(ev -> favDialog[0].dispose());
        favDialog[0].setContentPane(panel);
        favDialog[0].pack();
        favDialog[0].setLocationRelativeTo(this);
        favDialog[0].setVisible(true);
    }

    // ======================== Search History Management ========================

    private void loadSearchHistory() {
        try {
            Preferences prefs = Preferences.userNodeForPackage(UnifiedMainFrame.class);
            String histStr = prefs.get("search_history", "");
            searchHistory.clear();
            if (!histStr.isEmpty()) {
                for (String q : histStr.split("\\|")) {
                    if (!q.trim().isEmpty()) searchHistory.add(q.trim());
                }
            }
        } catch (Exception e) {
            addLog("加载搜索历史失败: " + e.getMessage());
        }
    }

    private void saveSearchHistory() {
        try {
            Preferences prefs = Preferences.userNodeForPackage(UnifiedMainFrame.class);
            prefs.put("search_history", String.join("|", searchHistory));
        } catch (Exception e) {
            addLog("保存搜索历史失败: " + e.getMessage());
        }
    }

    private void addToSearchHistory(String query) {
        if (query == null || query.isEmpty()) return;
        searchHistory.remove(query);
        searchHistory.add(0, query);
        if (searchHistory.size() > 50) {
            searchHistory = new java.util.ArrayList<>(searchHistory.subList(0, 50));
        }
        saveSearchHistory();
    }

    private void showSearchHistoryDialog() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setPreferredSize(new Dimension(420, 360));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("搜索历史 (" + searchHistory.size() + " 条)");
        title.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        title.setForeground(PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        DefaultListModel<String> histModel = new DefaultListModel<>();
        for (String q : searchHistory) histModel.addElement(q);
        JList<String> histList = new JList<>(histModel);
        histList.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        histList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(histList), BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        JButton searchBtn = createRoundedButton("搜索选中", PRIMARY, PRIMARY_HOVER, 100, 32);
        JButton clearHistBtn = createRoundedButton("清空历史", DANGER, DANGER_HOVER, 100, 32);
        JButton closeBtn = createRoundedButton("关闭", TEXT_MUTED, BG_DARK, 80, 32);
        btnRow.add(searchBtn);
        btnRow.add(clearHistBtn);
        btnRow.add(closeBtn);
        panel.add(btnRow, BorderLayout.SOUTH);

        searchBtn.addActionListener(ev -> {
            String sel = histList.getSelectedValue();
            if (sel != null) {
                searchField.setText(sel);
                doSearch();
            }
        });
        clearHistBtn.addActionListener(ev -> {
            searchHistory.clear();
            saveSearchHistory();
            histModel.clear();
            title.setText("搜索历史 (0 条)");
            addLog("搜索历史已清空");
        });
        final JDialog[] histDialog = new JDialog[1];
        histDialog[0] = new JDialog(this, "搜索历史", false);
        closeBtn.addActionListener(ev -> histDialog[0].dispose());
        histDialog[0].setContentPane(panel);
        histDialog[0].pack();
        histDialog[0].setLocationRelativeTo(this);
        histDialog[0].setVisible(true);
    }

    // ======================== File Preview ========================

    private void previewFile(KnowledgeItem item) {
        if (item == null) return;
        String name = cleanText(item.getTitle());
        String typeName = item.getMediaTypeInfo() != null ? item.getMediaTypeInfo().getName() : "未知";
        String ext = "";
        if (name.contains(".")) {
            int d = name.lastIndexOf(".");
            if (d < name.length() - 1) ext = name.substring(d + 1).toLowerCase();
        }

        // Image files: show preview dialog
        if (ext.matches("(jpg|jpeg|png|gif|bmp|webp|svg)")) {
            showImagePreview(item, name);
            return;
        }

        // All other files: show info preview
        showTextInfoPreview(item, name, typeName, ext);
    }

    private void showImagePreview(KnowledgeItem item, String name) {
        String jumpUrl = item.getJumpUrl();
        if (jumpUrl == null || jumpUrl.isEmpty()) {
            showTextInfoPreview(item, name, "图片", "img");
            return;
        }
        JDialog dialog = new JDialog(this, "预览: " + name, false);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel imgLabel = new JLabel("加载中...", SwingConstants.CENTER);
        imgLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        imgLabel.setForeground(TEXT_MUTED);
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(imgLabel, BorderLayout.CENTER);

        JButton closeBtn = createRoundedButton("关闭", TEXT_MUTED, BG_DARK, 80, 32);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(closeBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);

        new SwingWorker<ImageIcon, Void>() {
            @Override protected ImageIcon doInBackground() {
                try {
                    java.net.URL url = new java.net.URL(jumpUrl);
                    java.awt.Image img = javax.imageio.ImageIO.read(url);
                    if (img != null) {
                        int maxW = 560, maxH = 420;
                        int w = img.getWidth(null), h = img.getHeight(null);
                        if (w > maxW || h > maxH) {
                            double scale = Math.min((double) maxW / w, (double) maxH / h);
                            w = (int)(w * scale);
                            h = (int)(h * scale);
                        }
                        return new ImageIcon(img.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH));
                    }
                } catch (Exception e) {}
                return null;
            }
            @Override protected void done() {
                try {
                    ImageIcon icon = get();
                    if (icon != null) {
                        imgLabel.setText("");
                        imgLabel.setIcon(icon);
                    } else {
                        imgLabel.setText("图片加载失败");
                    }
                } catch (Exception e) { imgLabel.setText("加载出错"); }
            }
        }.execute();
    }

    private void showTextInfoPreview(KnowledgeItem item, String name, String typeName, String ext) {
        String size = item.getFileSize() != null ? formatFileSize(Long.parseLong(item.getFileSize().replaceAll("[^0-9]", ""))) : "未知";
        String updateTime = item.getUpdateTime() != null ? formatUpdateTime(item.getUpdateTime()) : "未知";
        String path = item.getSourcePath() != null ? item.getSourcePath() : "无路径";
        String jumpUrl = item.getJumpUrl() != null ? item.getJumpUrl() : "无";

        String html = "<html><body style='font-family: Microsoft YaHei; font-size: 13px; padding: 12px; color: #212b3c;'>"
            + "<h3 style='color: #4f8cff; margin-top: 0;'>预览: " + name + "</h3>"
            + "<hr style='border: 1px solid #e6ebf2;'>"
            + "<table style='width: 100%;'>"
            + "<tr><td style='color: #64748b; width: 80px;'>类型</td><td><b>" + typeName + "</b> (." + ext + ")</td></tr>"
            + "<tr><td style='color: #64748b;'>大小</td><td><b>" + size + "</b></td></tr>"
            + "<tr><td style='color: #64748b;'>路径</td><td>" + path + "</td></tr>"
            + "<tr><td style='color: #64748b;'>更新</td><td>" + updateTime + "</td></tr>"
            + "<tr><td style='color: #64748b;'>下载</td><td style='word-break:break-all;'>" + (jumpUrl.length() > 80 ? jumpUrl.substring(0,80)+"..." : jumpUrl) + "</td></tr>"
            + "</table></body></html>";

        JEditorPane editor = new JEditorPane("text/html", html);
        editor.setEditable(false);
        editor.setPreferredSize(new Dimension(480, 240));
        editor.setBackground(Color.WHITE);
        JOptionPane.showMessageDialog(this, editor, "文件预览", JOptionPane.INFORMATION_MESSAGE);
    }

    // ======================== Export Download List ========================

    private void exportDownloadList() {
        if (batchItemModel == null || batchItemModel.getSize() == 0) {
            JOptionPane.showMessageDialog(this, "当前无下载记录可导出", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object[] options = {"CSV (Excel兼容)", "TSV (Tab分隔)"};
        int choice = JOptionPane.showOptionDialog(this, "选择导出格式:", "导出下载清单",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        boolean useCSV = (choice == 0);
        String ext = useCSV ? ".csv" : ".tsv";
        String sep = useCSV ? "," : "\t";
        String lineSep = useCSV ? "\r\n" : "\n";

        JFileChooser chooser = new JFileChooser(config.getDownloadDir());
        chooser.setSelectedFile(new File("下载清单_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ext));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        String filePath = chooser.getSelectedFile().getAbsolutePath();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            if (!useCSV) {
                // TSV with BOM for Excel UTF-8 compatibility
                writer.write("\uFEFF");
            }
            writer.write("序号" + sep + "文件名" + sep + "类型" + sep + "大小" + sep + "路径" + sep + "状态" + lineSep);
            int idx = 1;
            for (int i = 0; i < batchItemModel.getSize(); i++) {
                KnowledgeItem item = batchItemModel.getElementAt(i);
                String name = cleanText(item.getTitle());
                String type = item.getMediaTypeInfo() != null ? item.getMediaTypeInfo().getName() : "未知";
                String sz = item.getFileSize() != null ? item.getFileSize() : "";
                String p = item.getSourcePath() != null ? item.getSourcePath() : "";
                String mid = item.getMediaId();
                String status = mid != null && itemStatusMap.containsKey(mid) ? itemStatusMap.get(mid) : "就绪";
                String line = idx + sep;
                line += name;
                line += sep + type + sep + sz + sep + p + sep + status + lineSep;
                writer.write(line);
                idx++;
            }
            addLog("已导出下载清单: " + filePath);
            JOptionPane.showMessageDialog(this, "导出成功!\n文件: " + filePath, "导出完成", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            addLog("导出失败: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "导出失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ======================== Proxy Settings Dialog ========================

    private void showProxySettingsDialog() {
        JDialog dialog = new JDialog(this, "代理设置", false);
        dialog.setSize(380, 260);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        formPanel.setBackground(Color.WHITE);

        JLabel typeLabel = new JLabel("代理类型:");
        typeLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        proxyTypeCombo = new JComboBox<>(new String[]{"无代理", "HTTP", "SOCKS"});
        proxyTypeCombo.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));

        JLabel hostLabel = new JLabel("代理地址:");
        hostLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        proxyHostField = new JTextField(config.getProxyHost());
        proxyHostField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));

        JLabel portLabel = new JLabel("端口:");
        portLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        proxyPortField = new JTextField(String.valueOf(config.getProxyPort()));
        proxyPortField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));

        formPanel.add(typeLabel); formPanel.add(proxyTypeCombo);
        formPanel.add(hostLabel); formPanel.add(proxyHostField);
        formPanel.add(portLabel); formPanel.add(proxyPortField);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        btnRow.setBackground(Color.WHITE);
        JButton saveBtn = createRoundedButton("保存", PRIMARY, PRIMARY_HOVER, 80, 32);
        JButton cancelBtn = createRoundedButton("取消", TEXT_MUTED, BG_DARK, 80, 32);
        btnRow.add(saveBtn);
        btnRow.add(cancelBtn);

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(btnRow, BorderLayout.SOUTH);
        dialog.setContentPane(panel);

        saveBtn.addActionListener(ev -> {
            String type = (String) proxyTypeCombo.getSelectedItem();
            String host = proxyHostField.getText().trim();
            int port = 0;
            try { port = Integer.parseInt(proxyPortField.getText().trim()); } catch (NumberFormatException ignored) {}

            if ("无代理".equals(type)) {
                config.setProxyType("none");
                downloadService.setGlobalProxy(Proxy.NO_PROXY);
            } else {
                config.setProxyType(type.toLowerCase());
                config.setProxyHost(host);
                config.setProxyPort(String.valueOf(port));
                try {
                    Proxy.Type ptype = "socks".equals(type.toLowerCase()) ? Proxy.Type.SOCKS : Proxy.Type.HTTP;
                    Proxy proxy = new Proxy(ptype, new java.net.InetSocketAddress(host, port));
                    downloadService.setGlobalProxy(proxy);
                    addLog("代理已设置: " + type + " " + host + ":" + port);
                } catch (Exception e) {
                    addLog("代理设置失败: " + e.getMessage());
                }
            }
            dialog.dispose();
        });
        cancelBtn.addActionListener(ev -> dialog.dispose());
        dialog.setVisible(true);
    }

    // ======================== Retry Failed Downloads ========================

    private void retryFailedDownloads() {
        List<KnowledgeItem> failedList = downloadService.getFailedItems();
        if (failedList == null || failedList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有失败的下载项", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "检测到 " + failedList.size() + " 个失败项，是否重新下载？",
            "重试确认", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        // Collect failed item IDs and restart batch download
        addBatchLog("重试 " + failedList.size() + " 个失败项...");
        if (selectedKB != null && !isBatchDownloading) {
            startBatchDownload(selectedKB.getKnowledgeBaseId(), cleanText(selectedKB.getTitle()));
        }
    }

    // ======================== UI Helpers ========================

    private JPanel createCardPanel() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Softer shadow (更柔和的阴影效果)
                g2.setColor(new Color(0, 0, 0, 6));
                g2.fillRoundRect(3, 4, getWidth() - 3, getHeight() - 3, 14, 14);
                g2.setColor(new Color(0, 0, 0, 3));
                g2.fillRoundRect(1, 2, getWidth() - 1, getHeight() - 1, 14, 14);
                // Card body
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setBackground(BG_PAGE);
        return p;
    }

    private JProgressBar createColoredProgressBar(Color startColor, Color endColor) {
        JProgressBar bar = new JProgressBar() {
            private final Color cs = startColor;
            private final Color ce = endColor;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                // Track background
                g2.setColor(new Color(240, 243, 248));
                g2.fillRoundRect(0, 0, w, h, h / 2, h / 2);
                // Fill
                int fillW = (int) ((double) getValue() / Math.max(1, getMaximum()) * w);
                if (fillW > 0) {
                    GradientPaint gp = new GradientPaint(0, 0, cs, fillW, 0, ce);
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, Math.max(fillW, h), h, h / 2, h / 2);
                }
                if (isStringPainted()) {
                    g2.setColor(getValue() > getMaximum() / 2 ? Color.WHITE : TEXT_SECONDARY);
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (w - fm.stringWidth(getString())) / 2;
                    int y = (h + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(getString(), x, y);
                }
                g2.dispose();
            }
        };
        bar.setStringPainted(true);
        bar.setFont(new Font("Microsoft YaHei", Font.PLAIN, 10));
        bar.setPreferredSize(new Dimension(0, 20));
        return bar;
    }

    private JButton createRoundedButton(String text, Color bg, Color hoverBg, int width, int height) {
        JButton button = new JButton(text) {
            private Color currentBg = bg;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Button shadow
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fillRoundRect(1, 2, getWidth() - 1, getHeight() - 1, height / 2, height / 2);
                // Button body with gradient
                int w = getWidth() - 1, h = getHeight() - 2;
                GradientPaint gp = new GradientPaint(0, 0, currentBg, 0, h, darken(currentBg, 15));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, w, h, height / 2, height / 2);
                // Text
                g2.setColor(getForeground());
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2 - 1;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
            @Override
            public void setBackground(Color bg2) { currentBg = bg2; repaint(); }
        };
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(width, height));
        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { button.setBackground(hoverBg); }
            @Override public void mouseExited(MouseEvent e) { button.setBackground(bg); }
        });
        return button;
    }

    private Color darken(Color c, int amount) {
        return new Color(Math.max(0, c.getRed() - amount), Math.max(0, c.getGreen() - amount), Math.max(0, c.getBlue() - amount));
    }

    private void pasteFromClipboard() {
        try {
            java.awt.datatransfer.Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            java.awt.datatransfer.Transferable t = cb.getContents(null);
            if (t != null && t.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.stringFlavor)) {
                inputTextArea.setText((String) t.getTransferData(java.awt.datatransfer.DataFlavor.stringFlavor));
                addLog("已从剪贴板粘贴");
            }
        } catch (Exception e) {
            addLog("粘贴失败: " + e.getMessage());
        }
    }

    private void openDownloadFolder() {
        try {
            String dir = config.getDownloadDir();
            if (dir.equals("./downloads")) dir = System.getProperty("user.dir") + File.separator + "downloads";
            File folder = new File(dir);
            if (!folder.exists()) folder.mkdirs();
            Desktop.getDesktop().open(folder);
        } catch (Exception e) {
            addLog("打开文件夹失败: " + e.getMessage());
        }
    }

    private void addLog(String message) {
        writeLogToFile("[UI] " + message);
        if (logTextArea != null) {
            String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
            SwingUtilities.invokeLater(() -> {
                logTextArea.append("[" + time + "] " + message + "\n");
                logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
                int lines = logTextArea.getLineCount();
                if (lines > config.getUiLogMaxLines()) {
                    try {
                        int start = logTextArea.getLineStartOffset(lines - config.getUiLogMaxLines());
                        logTextArea.replaceRange("", 0, start);
                    } catch (Exception ignored) {}
                }
            });
        }
    }

    private void addBatchLog(String message) {
        writeLogToFile("[批量下载] " + message);
        if (batchLogTextArea != null) {
            String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
            SwingUtilities.invokeLater(() -> {
                batchLogTextArea.append("[" + time + "] " + message + "\n");
                batchLogTextArea.setCaretPosition(batchLogTextArea.getDocument().getLength());
            });
        }
    }

    private String formatDuration(long ms) {
        if (ms < 1000) return ms + "ms";
        long s = ms / 1000;
        if (s < 60) return s + "s";
        long m = s / 60; s %= 60;
        if (m < 60) return m + "m" + s + "s";
        long h = m / 60; m %= 60;
        return h + "h" + m + "m" + s + "s";
    }

    private String formatFileSize(long size) {
        if (size <= 0) return "--";
        if (size < 1024) return size + "B";
        if (size < 1024 * 1024) return String.format("%.1fKB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.1fMB", size / (1024.0 * 1024));
        return String.format("%.2fGB", size / (1024.0 * 1024 * 1024));
    }

    private String cleanText(String text) {
        if (text == null) return "";
        text = HTML_TAG.matcher(text).replaceAll("");
        text = NON_ASCII_GARBAGE.matcher(text).replaceAll("");
        text = text.replaceAll("[\u00ef\u00bf\u00bd]", "");
        text = text.replaceAll("[\ufffd]", "");
        text = text.trim();
        return text.isEmpty() ? "" : text;
    }

    private String formatUpdateTime(String ts) {
        if (ts == null || ts.isEmpty()) return "";
        try {
            long sec = Long.parseLong(ts);
            if (sec > 1_000_000_000L) sec /= 1000;
            long now = System.currentTimeMillis() / 1000;
            long diff = now - sec;
            if (diff < 3600) return (diff / 60) + "分钟前";
            if (diff < 86400) return (diff / 3600) + "小时前";
            if (diff < 2592000) return (diff / 86400) + "天前";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(new Date(sec * 1000));
        } catch (NumberFormatException e) {
            return ts;
        }
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("文件");
        fileMenu.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        JMenuItem openItem = new JMenuItem("打开下载文件夹");
        openItem.addActionListener(e -> openDownloadFolder());
        JMenuItem exitItem = new JMenuItem("退出");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("帮助");
        helpMenu.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        JMenuItem usageItem = new JMenuItem("使用说明");
        usageItem.addActionListener(e -> showUsageGuide());
        JMenuItem aboutItem = new JMenuItem("关于");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "灵枢IMA智能下载器 V3.0\n\n功能特性:\n- 知识库搜索与发现\n- 知识库文件浏览\n- 分享ID下载\n- 知识库批量下载\n- 现代化圆润UI\n\n尽情探索!\n\n作者: 灵枢",
            "关于", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(usageItem);
        helpMenu.add(aboutItem);
        JMenu toolMenu = new JMenu("工具");
        toolMenu.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        JMenuItem proxyItem = new JMenuItem("代理设置");
        proxyItem.addActionListener(e -> showProxySettingsDialog());
        JMenuItem exportItem = new JMenuItem("导出下载清单");
        exportItem.addActionListener(e -> exportDownloadList());
        JMenuItem favMgmtItem = new JMenuItem("收藏夹管理");
        favMgmtItem.addActionListener(e -> showFavoritesDialog());
        JMenuItem historyMgmtItem = new JMenuItem("搜索历史");
        historyMgmtItem.addActionListener(e -> showSearchHistoryDialog());
        toolMenu.add(proxyItem);
        toolMenu.addSeparator();
        toolMenu.add(exportItem);
        toolMenu.addSeparator();
        toolMenu.add(favMgmtItem);
        toolMenu.add(historyMgmtItem);
        menuBar.add(fileMenu);
        menuBar.add(toolMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    // ======================== Cell Renderers ========================


    // Create a rounded-corner ImageIcon from a source image
    private ImageIcon makeRoundedIcon(Image img, int w, int h, int radius) {
        BufferedImage rounded = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = rounded.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, w, h, radius, radius));
        g2.drawImage(img.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
        g2.dispose();
        return new ImageIcon(rounded);
    }

    // Pre-generate rounded placeholder icon
    private ImageIcon makePlaceholderIcon() {
        BufferedImage ph = new BufferedImage(68, 68, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = ph.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(PRIMARY_LIGHT);
        g2.fillRoundRect(0, 0, 68, 68, 12, 12);
        g2.setColor(PRIMARY);
        g2.setFont(new Font(null, Font.BOLD, 18));
        java.awt.FontMetrics fm = g2.getFontMetrics();
        String text = "KB";
        int tx = (68 - fm.stringWidth(text)) / 2;
        int ty = (68 + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(text, tx, ty);
        g2.dispose();
        return new ImageIcon(ph);
    }

    private class SearchKBCellRenderer extends JPanel implements ListCellRenderer<SearchKBInfo> {
        private JLabel coverLabel;
        private JLabel titleLabel;
        private JLabel authorLabel;
        private JLabel descLabel;
        private JLabel statsLabel;
        private final Map<String, ImageIcon> coverCache = new HashMap<>();
        private final java.util.Set<String> loadingCovers = new java.util.HashSet<>();
        private static final int COVER_W = 68;
        private static final int COVER_H = 68;
        private final ImageIcon placeholderIcon = makePlaceholderIcon();

        SearchKBCellRenderer() {
            setLayout(new BorderLayout(10, 4));
            setOpaque(true);
            setBorder(new EmptyBorder(8, 12, 8, 12));

            coverLabel = new JLabel(placeholderIcon) {
                @Override public Dimension getPreferredSize() { return new Dimension(COVER_W, COVER_H); }
            };
            coverLabel.setOpaque(true);
            coverLabel.setBackground(Color.WHITE);

            JPanel rightPanel = new JPanel(new BorderLayout(0, 2));
            rightPanel.setOpaque(false);

            JPanel titleRow = new JPanel(new BorderLayout());
            titleRow.setOpaque(false);
            titleLabel = new JLabel();
            titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
            titleLabel.setForeground(TEXT_PRIMARY);
            authorLabel = new JLabel();
            authorLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 10));
            authorLabel.setForeground(TEXT_MUTED);
            titleRow.add(titleLabel, BorderLayout.WEST);
            titleRow.add(authorLabel, BorderLayout.EAST);
            rightPanel.add(titleRow, BorderLayout.NORTH);

            descLabel = new JLabel();
            descLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
            descLabel.setForeground(TEXT_SECONDARY);
            rightPanel.add(descLabel, BorderLayout.CENTER);

            statsLabel = new JLabel();
            statsLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 10));
            statsLabel.setForeground(TEXT_MUTED);
            rightPanel.add(statsLabel, BorderLayout.SOUTH);

            add(coverLabel, BorderLayout.WEST);
            add(rightPanel, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends SearchKBInfo> list, SearchKBInfo value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value == null) return this;
            String name = cleanText(value.getTitle());
            titleLabel.setText(name != null && !name.isEmpty() ? name : "Unknown KB");
            String author = cleanText(value.getAuthorName());
            authorLabel.setText(author != null && !author.isEmpty() ? author : "");
            String desc = cleanText(value.getDescription());
            if (desc != null && !desc.isEmpty()) {
                descLabel.setText(desc.length() > 80 ? desc.substring(0, 80) + "..." : desc);
                descLabel.setVisible(true);
            } else { descLabel.setText(""); descLabel.setVisible(false); }
            StringBuilder stats = new StringBuilder();
            stats.append(value.getFileCount()).append(" 个文件");
            if (value.getReadCount() > 0) stats.append(" · ").append(value.getReadCount()).append(" 成员");
            if (value.getShareCount() > 0) stats.append(" · ").append(value.getShareCount()).append(" 分享");
            if (value.getLikeCount() > 0) stats.append(" · ").append(value.getLikeCount()).append(" 点赞");
            String updateTime = value.getUpdateTime();
            if (updateTime != null && !updateTime.isEmpty()) stats.append(" · ").append(formatUpdateTime(updateTime));
            statsLabel.setText(stats.toString());
            String coverUrl = value.getCoverUrl();
            if (coverUrl != null && !coverUrl.isEmpty()) {
                if (coverCache.containsKey(coverUrl)) {
                    coverLabel.setIcon(coverCache.get(coverUrl));
                } else if (!loadingCovers.contains(coverUrl)) {
                    loadingCovers.add(coverUrl);
                    loadCoverAsync(coverUrl, list);
                }
            } else {
                coverLabel.setIcon(placeholderIcon);
            }

            // 滑动残留修复: 统一白色背景, 不再交替色
            if (isSelected) {
                setBackground(PRIMARY_LIGHT);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 3, 0, 0, PRIMARY),
                    new EmptyBorder(8, 9, 8, 12)));
            } else {
                setBackground(Color.WHITE);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT),
                    new EmptyBorder(8, 12, 8, 12)));
            }
            return this;
        }

        private void loadCoverAsync(final String url, final JList<? extends SearchKBInfo> list) {
            new SwingWorker<ImageIcon, Void>() {
                @Override protected ImageIcon doInBackground() {
                    try {
                        java.net.URL u = new java.net.URL(url);
                        java.awt.Image img = javax.imageio.ImageIO.read(u);
                        if (img != null) return makeRoundedIcon(img, COVER_W, COVER_H, 12);
                    } catch (Exception ignored) {}
                    return null;
                }
                @Override protected void done() {
                    try {
                        ImageIcon icon = get();
                        if (icon != null) {
                            coverCache.put(url, icon);
                            coverLabel.setIcon(icon);
                        }
                    } catch (Exception ignored) {}
                    loadingCovers.remove(url);
                    list.repaint();
                }
            }.execute();
        }
    }

    // ======================== BrowseItemCellRenderer ========================

    private class BrowseItemCellRenderer extends JPanel implements ListCellRenderer<KnowledgeItem> {
        private JLabel iconLabel;
        private JPanel textPanel;
        private JLabel nameLabel;
        private JLabel infoLabel;
        private JLabel pathLabel;
        private JLabel sizeTimeLabel;

        BrowseItemCellRenderer() {
            setLayout(new BorderLayout(8, 0));
            setBorder(new EmptyBorder(6, 12, 6, 12));
            setOpaque(true);

            iconLabel = new JLabel() {
                @Override public Dimension getPreferredSize() { return new Dimension(60, 38); }
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(), h = getHeight();
                    Object prop = getClientProperty("itemType");
                    boolean isFolder = "folder".equals(prop);
                    if (isFolder) {
                        g2.setColor(FOLDER_BG); g2.fillRoundRect(2, 2, w-4, h-4, 10, 10);
                        g2.setColor(FOLDER_BORDER); g2.drawRoundRect(2, 2, w-4, h-4, 10, 10);
                        g2.setFont(new Font("Microsoft YaHei", Font.BOLD, 11));
                        FontMetrics fm = g2.getFontMetrics();
                        g2.drawString("文件夹", (w-fm.stringWidth("文件夹"))/2, (h+fm.getAscent()-fm.getDescent())/2);
                    } else {
                        Object extProp = getClientProperty("fileExt");
                        Object typeProp = getClientProperty("fileTypeName");
                        String ext = (extProp != null) ? extProp.toString() : "F";
                        String tn = (typeProp != null) ? typeProp.toString() : "";
                        Color[] colors = getFileTypeColors(ext, tn);
                        g2.setColor(colors[0]); g2.fillRoundRect(2, 2, w-4, h-4, 10, 10);
                        g2.setColor(colors[1]); g2.drawRoundRect(2, 2, w-4, h-4, 10, 10);
                        String label = getFileTypeLabel(ext, tn);
                        g2.setFont(new Font("Microsoft YaHei", Font.BOLD, 10));
                        FontMetrics fm = g2.getFontMetrics();
                        g2.drawString(label, (w-fm.stringWidth(label))/2, (h+fm.getAscent()-fm.getDescent())/2);
                    }
                    g2.dispose();
                }
            };

            textPanel = new JPanel(new BorderLayout(0, 1));
            textPanel.setOpaque(false);

            nameLabel = new JLabel();
            nameLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
            nameLabel.setForeground(TEXT_PRIMARY);

            infoLabel = new JLabel();
            infoLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 10));
            infoLabel.setForeground(TEXT_MUTED);

            pathLabel = new JLabel();
            pathLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 10));
            pathLabel.setForeground(TEXT_MUTED);

            sizeTimeLabel = new JLabel();
            sizeTimeLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 10));
            sizeTimeLabel.setForeground(TEXT_MUTED);

            JPanel bottomRow = new JPanel(new BorderLayout(4, 0));
            bottomRow.setOpaque(false);
            bottomRow.add(pathLabel, BorderLayout.WEST);
            bottomRow.add(sizeTimeLabel, BorderLayout.EAST);

            textPanel.add(nameLabel, BorderLayout.NORTH);
            textPanel.add(infoLabel, BorderLayout.CENTER);
            textPanel.add(bottomRow, BorderLayout.SOUTH);

            add(iconLabel, BorderLayout.WEST);
            add(textPanel, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends KnowledgeItem> list, KnowledgeItem value, int index, boolean isSelected, boolean cellHasFocus) {
            String name = cleanText(value.getTitle());
            nameLabel.setText(name != null && !name.isEmpty() ? name : "Unknown");
            boolean isFolder = value.getMediaId() != null && value.getMediaId().startsWith("folder_");
            if (!isFolder && value.getMediaTypeInfo() != null && "文件夹".equals(value.getMediaTypeInfo().getName())) isFolder = true;
            putClientProperty("itemType", isFolder ? "folder" : "file");

            if (isFolder) {
                infoLabel.setText("双击浏览子文件夹");
                infoLabel.setForeground(TEXT_MUTED);
                pathLabel.setText(value.getSourcePath() != null ? value.getSourcePath() : "");
                sizeTimeLabel.setText("");
            } else {
                String typeName = "文件";
                if (value.getMediaTypeInfo() != null && value.getMediaTypeInfo().getName() != null) typeName = value.getMediaTypeInfo().getName();
                String ext = "";
                if (name.contains(".")) { int d = name.lastIndexOf("."); if (d < name.length()-1) ext = name.substring(d+1).toUpperCase(); if (ext.length()>4) ext=ext.substring(0,4); }
                if (ext.isEmpty()) ext = typeName.length()<=4 ? typeName : typeName.substring(0,2);
                putClientProperty("fileExt", ext);
                putClientProperty("fileTypeName", typeName);

                infoLabel.setText(typeName + " | " + (value.getJumpUrl() != null ? "[可下载]" : "[无链接]"));
                infoLabel.setForeground(value.getJumpUrl() != null ? SUCCESS : DANGER);
                pathLabel.setText(value.getSourcePath() != null ? value.getSourcePath() : "");

                StringBuilder st = new StringBuilder();
                if (value.getFileSize() != null && !value.getFileSize().isEmpty() && !"0".equals(value.getFileSize())) {
                    try { st.append(formatFileSize(Long.parseLong(value.getFileSize()))); } catch (NumberFormatException e) {}
                }
                if (value.getUpdateTime() != null && !value.getUpdateTime().isEmpty()) {
                    if (st.length() > 0) st.append(" · ");
                    st.append(formatUpdateTime(value.getUpdateTime()));
                }
                sizeTimeLabel.setText(st.toString());
            }

            // 滑动残留修复: 统一白色背景
            if (isSelected) {
                setBackground(BROWSE_PURPLE_LIGHT);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 3, 0, 0, BROWSE_PURPLE),
                    new EmptyBorder(6, 9, 6, 12)));
            } else {
                setBackground(Color.WHITE);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT),
                    new EmptyBorder(6, 12, 6, 12)));
            }
            iconLabel.putClientProperty("itemType", getClientProperty("itemType"));
            iconLabel.putClientProperty("fileExt", getClientProperty("fileExt"));
            iconLabel.repaint();
            return this;
        }
    }

    // ======================== BatchItemCellRenderer ========================

    private class BatchItemCellRenderer extends JPanel implements ListCellRenderer<KnowledgeItem> {
        private JLabel iconLabel;
        private JPanel textPanel;
        private JLabel nameLabel;
        private JLabel infoLabel;
        private JLabel pathLabel;
        private JLabel sizeTimeLabel;
        private JProgressBar itemProgressBar;

        BatchItemCellRenderer() {
            setLayout(new BorderLayout(8, 0));
            setBorder(new EmptyBorder(4, 12, 4, 12));
            setOpaque(true);

            iconLabel = new JLabel() {
                @Override public Dimension getPreferredSize() { return new Dimension(54, 34); }
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(), h = getHeight();
                    Object prop = getClientProperty("itemType");
                    boolean isFolder = "folder".equals(prop);
                    if (isFolder) {
                        g2.setColor(FOLDER_BG); g2.fillRoundRect(2, 2, w-4, h-4, 8, 8);
                        g2.setColor(FOLDER_BORDER); g2.drawRoundRect(2, 2, w-4, h-4, 8, 8);
                        g2.setFont(new Font("Microsoft YaHei", Font.BOLD, 10));
                        FontMetrics fm = g2.getFontMetrics();
                        g2.drawString("文件夹", (w-fm.stringWidth("文件夹"))/2, (h+fm.getAscent()-fm.getDescent())/2);
                    } else {
                        Object extProp = getClientProperty("fileExt");
                        Object typeProp = getClientProperty("fileTypeName");
                        String ext = (extProp != null) ? extProp.toString() : "F";
                        String tn = (typeProp != null) ? typeProp.toString() : "";
                        Color[] colors = getFileTypeColors(ext, tn);
                        g2.setColor(colors[0]); g2.fillRoundRect(2, 2, w-4, h-4, 8, 8);
                        g2.setColor(colors[1]); g2.drawRoundRect(2, 2, w-4, h-4, 8, 8);
                        String label = getFileTypeLabel(ext, tn);
                        g2.setFont(new Font("Microsoft YaHei", Font.BOLD, 10));
                        FontMetrics fm = g2.getFontMetrics();
                        g2.drawString(label, (w-fm.stringWidth(label))/2, (h+fm.getAscent()-fm.getDescent())/2);
                    }
                    g2.dispose();
                }
            };

            textPanel = new JPanel(new BorderLayout(0, 1));
            textPanel.setOpaque(false);

            nameLabel = new JLabel();
            nameLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 11));
            nameLabel.setForeground(TEXT_PRIMARY);

            JPanel midRow = new JPanel(new BorderLayout(4, 0));
            midRow.setOpaque(false);
            infoLabel = new JLabel();
            infoLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 10));
            infoLabel.setForeground(TEXT_MUTED);
            sizeTimeLabel = new JLabel();
            sizeTimeLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 10));
            sizeTimeLabel.setForeground(TEXT_MUTED);
            midRow.add(infoLabel, BorderLayout.WEST);
            midRow.add(sizeTimeLabel, BorderLayout.EAST);

            pathLabel = new JLabel();
            pathLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 10));
            pathLabel.setForeground(TEXT_MUTED);

            itemProgressBar = new JProgressBar(0, 100) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(), h = getHeight();
                    g2.setColor(new Color(240, 243, 248));
                    g2.fillRoundRect(0, 0, w, h, h/2, h/2);
                    int fillW = (int) ((double) getValue() / Math.max(1, getMaximum()) * w);
                    if (fillW > 0 && getValue() > 0) {
                        g2.setColor(BATCH_GREEN);
                        g2.fillRoundRect(0, 0, Math.max(fillW, h), h, h/2, h/2);
                    }
                    g2.dispose();
                }
            };
            itemProgressBar.setPreferredSize(new Dimension(0, 4));
            itemProgressBar.setVisible(false);

            textPanel.add(nameLabel, BorderLayout.NORTH);
            textPanel.add(midRow, BorderLayout.CENTER);
            textPanel.add(pathLabel, BorderLayout.CENTER);
            textPanel.add(itemProgressBar, BorderLayout.SOUTH);

            add(iconLabel, BorderLayout.WEST);
            add(textPanel, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends KnowledgeItem> list, KnowledgeItem value, int index, boolean isSelected, boolean cellHasFocus) {
            String name = cleanText(value.getTitle());
            nameLabel.setText(name != null && !name.isEmpty() ? name : "Unknown");
            boolean isFolder = value.getMediaId() != null && value.getMediaId().startsWith("folder_");
            if (!isFolder && value.getMediaTypeInfo() != null && "文件夹".equals(value.getMediaTypeInfo().getName())) isFolder = true;
            putClientProperty("itemType", isFolder ? "folder" : "file");

            if (isFolder) {
                infoLabel.setText("文件夹");
                pathLabel.setText("");
                sizeTimeLabel.setText("");
                itemProgressBar.setVisible(false);
                putClientProperty("fileExt", "");
            } else {
                String typeName = "文件";
                if (value.getMediaTypeInfo() != null && value.getMediaTypeInfo().getName() != null) typeName = value.getMediaTypeInfo().getName();
                String ext = "";
                if (name.contains(".")) { int d = name.lastIndexOf("."); if (d < name.length()-1) ext = name.substring(d+1).toUpperCase(); if (ext.length()>4) ext=ext.substring(0,4); }
                if (ext.isEmpty()) ext = typeName.length()<=4 ? typeName : typeName.substring(0,2);
                putClientProperty("fileExt", ext);
                putClientProperty("fileTypeName", typeName);

                String mid = value.getMediaId();
                String statusText = "就绪";
                if (mid != null && itemStatusMap.containsKey(mid)) statusText = itemStatusMap.get(mid);
                infoLabel.setText(typeName + " | " + statusText);
                infoLabel.setForeground(getStatusColor(statusText));

                pathLabel.setText(value.getSourcePath() != null ? value.getSourcePath() : "");

                StringBuilder st = new StringBuilder();
                if (value.getFileSize() != null && !value.getFileSize().isEmpty() && !"0".equals(value.getFileSize())) {
                    try { st.append(formatFileSize(Long.parseLong(value.getFileSize()))); } catch (NumberFormatException e) {}
                }
                if (value.getUpdateTime() != null && !value.getUpdateTime().isEmpty()) {
                    if (st.length() > 0) st.append(" · ");
                    st.append(formatUpdateTime(value.getUpdateTime()));
                }
                sizeTimeLabel.setText(st.toString());

                int pv = 0;
                if (mid != null && itemProgressMap.containsKey(mid)) pv = itemProgressMap.get(mid);
                itemProgressBar.setVisible(true);
                itemProgressBar.setValue(pv);
            }

            // 滑动残留修复: 统一白色背景
            if (isSelected) {
                setBackground(PRIMARY_LIGHT);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 3, 0, 0, PRIMARY),
                    new EmptyBorder(4, 9, 4, 12)));
            } else {
                setBackground(Color.WHITE);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT),
                    new EmptyBorder(4, 12, 4, 12)));
            }
            iconLabel.putClientProperty("itemType", getClientProperty("itemType"));
            iconLabel.putClientProperty("fileExt", getClientProperty("fileExt"));
            iconLabel.repaint();
            return this;
        }

        private Color getStatusColor(String status) {
            if ("已完成".equals(status)) return SUCCESS;
            if ("已存在".equals(status)) return WARNING;
            if (status != null && status.startsWith("下载失败")) return DANGER;
            if ("下载中".equals(status)) return PRIMARY;
            return TEXT_MUTED;
        }
    }


    private String getFileTypeLabel(String ext, String typeName) {
        String e = ext.toLowerCase();
        if ("pdf".equals(e) || typeName.contains("PDF")) return "PDF";
        if (e.matches("(doc|docx)") || typeName.contains("Word")) return "Word";
        if (e.matches("(xls|xlsx)") || typeName.contains("Excel")) return "Excel";
        if (e.matches("(ppt|pptx)") || typeName.contains("PPT") || typeName.contains("幻灯")) return "PPT";
        if (e.matches("(jpg|jpeg|png|gif|bmp|webp|svg)")) return "图片";
        if (e.matches("(mp4|avi|mkv|mov|wmv|flv)")) return "视频";
        if (e.matches("(mp3|wav|flac|aac|ogg|wma|m4a)")) return "音频";
        if (e.matches("(zip|rar|7z|tar|gz)")) return "压缩";
        if (e.matches("(java|py|js|html|css|json|xml|sql)")) return "代码";
        if (e.matches("(txt|md|log)")) return "文本";
        if (typeName.contains("图片") || typeName.contains("Image")) return "图片";
        if (typeName.contains("视频") || typeName.contains("Video")) return "视频";
        if (typeName.contains("音频") || typeName.contains("Audio")) return "音频";
        return ext.length() <= 4 ? ext.toUpperCase() : ext.substring(0, 4).toUpperCase();
    }
    // ======================== Main ========================

    public static void main(String[] args) {
        try {
        } catch (Exception ignored) {}
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }
        SwingUtilities.invokeLater(() -> {
            UnifiedMainFrame frame = new UnifiedMainFrame();
            try {
                if (Taskbar.isTaskbarSupported()) {
                    Taskbar taskbar = Taskbar.getTaskbar();
                    java.net.URL iconUrl = UnifiedMainFrame.class.getClassLoader().getResource("icon/icon_256.png");
                    if (iconUrl != null) taskbar.setIconImage(new javax.swing.ImageIcon(iconUrl).getImage());
                }
                java.net.URL iconUrl = UnifiedMainFrame.class.getClassLoader().getResource("icon/icon_256.png");
                if (iconUrl != null) frame.setIconImage(new javax.swing.ImageIcon(iconUrl).getImage());
            } catch (Exception ignored) {}
            frame.setVisible(true);
        });
    }
}
