//版权声明
//本软件由作者:灵枢 开发，仅供学习交流使用
//严禁任何形式的非法破解、反编译、逆向工程等行为
//使用者应当遵守相关法律法规，尊重知识产权
//作者:灵枢 保留所有权利

package com.lingshu.ima.ui;

import com.lingshu.ima.config.AppConfig;
import com.lingshu.ima.service.DownloadService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;

//版权声明
//本软件由作者:灵枢 开发，仅供学习交流使用
//严禁任何形式的非法破解、反编译、逆向工程等行为
//使用者应当遵守相关法律法规，尊重知识产权
//作者:灵枢 保留所有权利

public class UnifiedMainFrame extends JFrame {
    
    private final AppConfig config;
    private final DownloadService downloadService;
    
    private JTextArea inputTextArea;
    private JTextArea pasteTextArea;
    private JButton fetchButton;
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
    
    private int totalFiles = 0;
    private int successCount = 0;
    private int failedCount = 0;
    private int existingCount = 0;
    
    private java.util.List<String> originalShareIds = new java.util.ArrayList<>();
    private java.util.Map<String, String> shareIdToFolderName = new java.util.HashMap<>();
    private java.util.Map<String, String> currentFolderMap = new java.util.HashMap<>();
    private java.util.Map<String, String> fileNameToFolderId = new java.util.HashMap<>();
    
    private static class PasteFileInfo {
        String fileName;
        String filePath;
        String downloadUrl;
        
        PasteFileInfo(String fileName, String filePath, String downloadUrl) {
            this.fileName = fileName;
            this.filePath = filePath;
            this.downloadUrl = downloadUrl;
        }
    }
    
    public UnifiedMainFrame() {
        this.config = AppConfig.getInstance();
        this.downloadService = new DownloadService();
        
        initializeUI();
        setupEventListeners();
        addLog("灵枢IMA智能下载器V2.2已启动 [" + getCurrentTime() + "]");
        
        showUsageDialog();
    }
    
    //版权声明
    //本软件由作者:灵枢 开发，仅供学习交流使用
    //严禁任何形式的非法破解、反编译、逆向工程等行为
    //使用者应当遵守相关法律法规，尊重知识产权
    //作者:灵枢 保留所有权利
    
    private void initializeUI() {
        setTitle(config.getAppName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        mainPanel.add(createTopPanel(), BorderLayout.NORTH);
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createBottomPanel(), BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("输入区域"));
        
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        
        JLabel titleLabel = new JLabel("请输入ShareID或URL（每行一个）：");
        inputPanel.add(titleLabel, BorderLayout.NORTH);
        
        inputTextArea = new JTextArea(3, 80);
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        
        JPanel pastePanel = new JPanel(new BorderLayout(5, 5));
        pastePanel.setBorder(BorderFactory.createTitledBorder("粘贴下载区域"));
        
        JLabel pasteLabel = new JLabel("粘贴下载链接（自动识别URL）：");
        pastePanel.add(pasteLabel, BorderLayout.NORTH);
        
        pasteTextArea = new JTextArea(4, 80);
        pasteTextArea.setLineWrap(true);
        pasteTextArea.setWrapStyleWord(true);
        pasteTextArea.setEditable(true);
        JScrollPane pasteScrollPane = new JScrollPane(pasteTextArea);
        pastePanel.add(pasteScrollPane, BorderLayout.CENTER);
        
        panel.add(pastePanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        fetchButton = new JButton("读取文件列表");
        fetchButton.setPreferredSize(new Dimension(150, 35));

        startDownloadButton = new JButton("开始下载");
        startDownloadButton.setPreferredSize(new Dimension(150, 35));

        stopDownloadButton = new JButton("停止下载");
        stopDownloadButton.setPreferredSize(new Dimension(150, 35));
        stopDownloadButton.setEnabled(false);

        fastDownloadCheckBox = new JCheckBox("快速下载模式");
        fastDownloadCheckBox.setToolTipText("启用后直接使用下载链接，跳过API验证");
        fastDownloadCheckBox.setSelected(true);

        buttonPanel.add(fetchButton);
        buttonPanel.add(startDownloadButton);
        buttonPanel.add(stopDownloadButton);
        buttonPanel.add(fastDownloadCheckBox);
        
        pasteButton = new JButton("粘贴并下载");
        pasteButton.setPreferredSize(new Dimension(150, 35));
        pasteButton.setToolTipText("从剪贴板粘贴文本并自动识别下载链接");
        buttonPanel.add(pasteButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6);
        
        splitPane.setTopComponent(createFileListPanel());
        splitPane.setBottomComponent(createLogPanel());
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(splitPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFileListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("文件列表"));
        
        String[] columnNames = {"文件类型", "文件名", "路径", "状态/进度", "速度", "文件大小", "权限", "耗时", "下载链接", "知识库ID"};
        fileListModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        fileListTable = new JTable(fileListModel);
        fileListTable.setAutoCreateRowSorter(true);
        fileListTable.setRowHeight(28);
        fileListTable.getColumnModel().getColumn(3).setCellRenderer(new ProgressBarRenderer());
        
        JScrollPane tableScrollPane = new JScrollPane(fileListTable);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("日志记录"));
        panel.setPreferredSize(new Dimension(400, 300));
        
        logTextArea = new JTextArea(15, 50);
        logTextArea.setEditable(false);
        logTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logScrollPane = new JScrollPane(logTextArea);
        panel.add(logScrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        JPanel progressPanel = new JPanel(new BorderLayout(5, 5));
        
        totalProgressBar = new JProgressBar(0, 100);
        totalProgressBar.setStringPainted(true);
        totalProgressBar.setString("总进度: 0%");
        totalProgressBar.setPreferredSize(new Dimension(400, 30));
        
        totalProgressLabel = new JLabel("共 0 个，已完成 0 个，失败 0 个");
        totalProgressLabel.setPreferredSize(new Dimension(400, 25));
        
        currentFileProgressBar = new JProgressBar(0, 100);
        currentFileProgressBar.setStringPainted(true);
        currentFileProgressBar.setString("当前文件: 等待中");
        currentFileProgressBar.setPreferredSize(new Dimension(400, 25));
        
        currentFileLabel = new JLabel("当前文件: 无");
        currentFileLabel.setPreferredSize(new Dimension(400, 20));
        
        JPanel totalProgressPanel = new JPanel(new BorderLayout(5, 5));
        totalProgressPanel.add(totalProgressBar, BorderLayout.CENTER);
        totalProgressPanel.add(totalProgressLabel, BorderLayout.SOUTH);
        
        JPanel currentFileProgressPanel = new JPanel(new BorderLayout(5, 5));
        currentFileProgressPanel.add(currentFileProgressBar, BorderLayout.CENTER);
        currentFileProgressPanel.add(currentFileLabel, BorderLayout.SOUTH);
        
        progressPanel.add(totalProgressPanel, BorderLayout.NORTH);
        progressPanel.add(currentFileProgressPanel, BorderLayout.CENTER);
        
        panel.add(progressPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        openFolderButton = new JButton("打开下载文件夹");
        openFolderButton.setPreferredSize(new Dimension(150, 35));
        
        clearLogButton = new JButton("清空日志");
        clearLogButton.setPreferredSize(new Dimension(150, 35));
        
        buttonPanel.add(openFolderButton);
        buttonPanel.add(clearLogButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    //版权声明
    //本软件由作者:灵枢 开发，仅供学习交流使用
    //严禁任何形式的非法破解、反编译、逆向工程等行为
    //使用者应当遵守相关法律法规，尊重知识产权
    //作者:灵枢 保留所有权利
    
    private void setupEventListeners() {
        fetchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchFileList();
            }
        });
        
        startDownloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startDownload();
            }
        });
        
        stopDownloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopDownload();
            }
        });
        
        openFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDownloadFolder();
            }
        });
        
        clearLogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearLog();
            }
        });
        
        pasteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pasteAndDownloadLinks();
            }
        });
        
        fileListTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleDoubleClick();
                }
            }
        });
    }
    
    private void showPopupMenu(MouseEvent e) {
        JPopupMenu popupMenu = new JPopupMenu();
        
        JMenuItem singleDownloadItem = new JMenuItem("单独下载");
        singleDownloadItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                downloadSelectedFiles(false);
            }
        });
        
        JMenuItem batchDownloadItem = new JMenuItem("批量下载");
        batchDownloadItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                downloadSelectedFiles(true);
            }
        });
        
        JMenuItem copyUrlItem = new JMenuItem("复制下载链接");
        copyUrlItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copySelectedUrls();
            }
        });
        
        popupMenu.add(singleDownloadItem);
        popupMenu.add(batchDownloadItem);
        popupMenu.addSeparator();
        popupMenu.add(copyUrlItem);
        
        popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }
    
    private void handleDoubleClick() {
        int selectedRow = fileListTable.getSelectedRow();
        if (selectedRow == -1) return;

        String fileType = (String) fileListModel.getValueAt(selectedRow, 0);
        String fileName = (String) fileListModel.getValueAt(selectedRow, 1);
        String shareId = (String) fileListModel.getValueAt(selectedRow, 9);

        if ("文件夹".equals(fileType)) {
            addLog("双击文件夹: " + fileName + "，准备查看内容 [" + getCurrentTime() + "]");
            showFolderContent(shareId, fileName, selectedRow);
        } else {
            addLog("双击文件: " + fileName + "，不是文件夹 [" + getCurrentTime() + "]");
        }
    }
    
    private void showFolderContent(String shareId, String folderName, int selectedRow) {
        new Thread(() -> {
            try {
                addLog("正在获取文件夹内容: " + folderName + " [" + getCurrentTime() + "]");
                addLog("ShareID: " + shareId + " [" + getCurrentTime() + "]");
                String folderId = null;
                java.util.List<com.lingshu.ima.dto.helper.KnowledgeItem> allItems = null;
                
                try {
                    allItems = downloadService.getApiService().fetchShareInfo(shareId);
                } catch (Exception e) {
                    addLog("【警告】获取文件列表失败: " + e.getMessage() + " [" + getCurrentTime() + "]");
                    SwingUtilities.invokeLater(() -> {
                        addLog("【错误】无法获取文件夹ID: " + folderName + " [" + getCurrentTime() + "]");
                    });
                    return;
                }
                
                if (allItems == null || allItems.isEmpty()) {
                    SwingUtilities.invokeLater(() -> {
                        addLog("【错误】无法获取文件列表: " + folderName + " [" + getCurrentTime() + "]");
                    });
                    return;
                }
                
                for (com.lingshu.ima.dto.helper.KnowledgeItem item : allItems) {
                    if (folderName.equals(item.getTitle()) && "99".equals(item.getMediaType())) {
                        folderId = item.getMediaId();
                        addLog("找到文件夹ID: " + folderId + " [" + getCurrentTime() + "]");
                        break;
                    }
                }
                
                if (folderId == null || folderId.isEmpty()) {
                    SwingUtilities.invokeLater(() -> {
                        addLog("【错误】无法获取文件夹ID: " + folderName + " [" + getCurrentTime() + "]");
                    });
                    return;
                }
                
                addLog("开始递归获取文件夹内容，文件夹ID: " + folderId + " [" + getCurrentTime() + "]");
                final java.util.List<com.lingshu.ima.dto.helper.KnowledgeItem> items;
                try {
                    items = downloadService.fetchAllFolderItemsRecursive(shareId, folderId, folderName);
                } catch (Exception e) {
                    addLog("【错误】递归获取文件夹内容失败: " + e.getMessage() + " [" + getCurrentTime() + "]");
                    SwingUtilities.invokeLater(() -> {
                        addLog("【错误】无法获取文件夹内容: " + folderName + " [" + getCurrentTime() + "]");
                    });
                    return;
                }
                
                if (items == null || items.isEmpty()) {
                    SwingUtilities.invokeLater(() -> {
                        addLog("【警告】文件夹为空: " + folderName + " [" + getCurrentTime() + "]");
                    });
                    return;
                }
                
                SwingUtilities.invokeLater(() -> {
                    addLog("文件夹 " + folderName + " 包含 " + items.size() + " 个项目（包括子文件夹） [" + getCurrentTime() + "]");
                    showFolderContentDialog(folderName, items, shareId);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    addLog("【错误】获取文件夹内容失败: " + e.getMessage() + " [" + getCurrentTime() + "]");
                });
            }
        }).start();
    }
    
    private void showFolderContentDialog(String folderName, java.util.List<com.lingshu.ima.dto.helper.KnowledgeItem> items, String shareId) {
        JDialog dialog = new JDialog(this, "文件夹内容: " + folderName, true);
        dialog.setSize(1400, 700);
        dialog.setLocationRelativeTo(this);

        String[] columnNames = {"文件类型", "文件名", "路径", "文件大小", "权限", "下载链接"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (com.lingshu.ima.dto.helper.KnowledgeItem item : items) {
            String fileType = getFileType(item);
            String fileName = item.getTitle();
            String filePath = item.getSourcePath() != null ? item.getSourcePath() : "";
            String fileSizeStr = item.getFileSize();
            long fileSize = 0;

            if (fileSizeStr != null && !fileSizeStr.isEmpty()) {
                try {
                    fileSize = Long.parseLong(fileSizeStr);
                } catch (NumberFormatException e) {
                    fileSize = 0;
                }
            }

            String fileSizeFormatted = formatFileSize(fileSize);
            String permission = getPermission(item);
            String jumpUrl = item.getJumpUrl();

            model.addRow(new Object[]{fileType, fileName, filePath, fileSizeFormatted, permission, jumpUrl});
        }

        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setRowHeight(28);
        table.getColumnModel().getColumn(5).setPreferredWidth(400);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showFolderPopupMenu(e, table, model);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showFolderPopupMenu(e, table, model);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton copyAllLinksButton = new JButton("复制所有链接");
        copyAllLinksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyFolderAllLinks(model, folderName);
            }
        });
        
        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        buttonPanel.add(copyAllLinksButton);
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
    
    private void fetchFileList() {
        String input = inputTextArea.getText().trim();
        
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入ShareID或URL", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] lines = input.split("\n");
        java.util.List<String> shareIds = new java.util.ArrayList<>();
        
        for (String line : lines) {
            String shareId = downloadService.getApiService().extractShareId(line);
            if (shareId != null && !shareId.isEmpty()) {
                shareIds.add(shareId);
            }
        }
        
        if (shareIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "未找到有效的ShareID", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        addLog("开始读取文件列表，共 " + shareIds.size() + " 个ShareID [" + getCurrentTime() + "]");
        
        fileListModel.setRowCount(0);
        totalFiles = 0;
        
        for (String shareId : shareIds) {
            fetchSingleShareId(shareId);
        }
    }
    
    private void fetchSingleShareId(String shareId) {
        new Thread(() -> {
            try {
                addLog("正在读取文件列表... [" + getCurrentTime() + "]");
                
                // 获取知识库名称
                String folderName = getKnowledgeBaseName(shareId);
                shareIdToFolderName.put(shareId, folderName);
                
                java.util.List<com.lingshu.ima.dto.helper.KnowledgeItem> items = downloadService.getApiService().fetchShareInfo(shareId);
                
                SwingUtilities.invokeLater(() -> {
                    for (com.lingshu.ima.dto.helper.KnowledgeItem item : items) {
                        String fileType = getFileType(item);
                        String fileName = item.getTitle();
                        String filePath = item.getSourcePath() != null ? item.getSourcePath() : "";
                        String fileSizeStr = item.getFileSize();
                        long fileSize = 0;
                        
                        if (fileSizeStr != null && !fileSizeStr.isEmpty()) {
                            try {
                                fileSize = Long.parseLong(fileSizeStr);
                            } catch (NumberFormatException e) {
                                fileSize = 0;
                            }
                        }
                        
                        String fileSizeFormatted = formatFileSize(fileSize);
                        String permission = getPermission(item);
                        String jumpUrl = item.getJumpUrl();
                        
                        fileListModel.addRow(new Object[]{fileType, fileName, filePath, 0, "0 KB/s", fileSizeFormatted, permission, "0:00", jumpUrl, shareId});
                        totalFiles++;
                    }
                    
                    addLog("ShareID " + shareId + " 读取到 " + items.size() + " 个文件 [" + getCurrentTime() + "]");
                    updateTotalProgress();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    addLog("【错误】读取失败: " + e.getMessage() + " [" + getCurrentTime() + "]");
                });
            }
        }).start();
    }
    
    private String getKnowledgeBaseName(String shareId) {
        try {
            com.lingshu.ima.dto.ShareInfoResponse response = downloadService.getApiService().getShareInfo(shareId, 1, "", "");
            if (response.getCurrentPath() != null && !response.getCurrentPath().isEmpty()) {
                String folderName = response.getCurrentPath().get(0).getFolderName();
                if (folderName != null && !folderName.isEmpty()) {
                    return folderName + "-" + shareId.substring(0, 8);
                }
            }
        } catch (Exception e) {
            System.err.println("获取知识库名称失败: " + e.getMessage());
        }
        return "知识库-" + shareId.substring(0, 8);
    }
    
    private void startDownload() {
        if (fileListModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "请先读取文件列表", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        boolean fastMode = fastDownloadCheckBox.isSelected();
        addLog("开始批量下载，模式: " + (fastMode ? "快速模式" : "标准模式") + " [" + getCurrentTime() + "]");

        java.util.List<String> shareIds = new java.util.ArrayList<>();
        java.util.List<com.lingshu.ima.dto.helper.KnowledgeItem> allItems = new java.util.ArrayList<>();

        for (int i = 0; i < fileListModel.getRowCount(); i++) {
            String jumpUrl = (String) fileListModel.getValueAt(i, 8);
            String shareId = (String) fileListModel.getValueAt(i, 9);
            String filePath = (String) fileListModel.getValueAt(i, 2);

            if (shareId != null && !shareIds.contains(shareId)) {
                shareIds.add(shareId);
            }

            com.lingshu.ima.dto.helper.KnowledgeItem item = new com.lingshu.ima.dto.helper.KnowledgeItem();
            item.setTitle((String) fileListModel.getValueAt(i, 1));
            item.setFileSize(String.valueOf(parseFileSize((String) fileListModel.getValueAt(i, 6))));
            item.setJumpUrl(jumpUrl);
            item.setSourcePath(filePath);
            allItems.add(item);
        }

        if (shareIds.isEmpty()) {
            addLog("【错误】无法提取ShareID，请检查下载链接是否有效");
            startDownloadButton.setEnabled(true);
            stopDownloadButton.setEnabled(false);
            return;
        }

        addLog("提取到 " + shareIds.size() + " 个ShareID [" + getCurrentTime() + "]");

        startDownloadButton.setEnabled(false);
        stopDownloadButton.setEnabled(true);
        
        currentFileProgressBar.setValue(0);
        currentFileProgressBar.setString("当前文件: 等待中");
        currentFileLabel.setText("当前文件: 无");

        if (fastMode) {
            downloadService.downloadFastMode(shareIds, allItems, new DownloadService.DownloadProgressCallback() {
                @Override
                public void onProgress(DownloadService.DownloadProgress progress) {
                    SwingUtilities.invokeLater(() -> {
                        addOrUpdateTableRow(progress);
                    });
                }

                @Override
                public void onComplete(int total, int success, int failed, int existing, long duration) {
                    SwingUtilities.invokeLater(() -> {
                        successCount += success;
                        failedCount += failed;
                        existingCount += existing;
                        updateTotalProgress();

                        addLog("下载完成 - 总数: " + total + ", 成功: " + success + ", 失败: " + failed + ", 已存在: " + existing + " [" + getCurrentTime() + "]");

                        startDownloadButton.setEnabled(true);
                        stopDownloadButton.setEnabled(false);
                        
                        currentFileProgressBar.setValue(0);
                        currentFileProgressBar.setString("当前文件: 等待中");
                        currentFileLabel.setText("当前文件: 无");

                        showCompleteDialog(totalFiles, successCount, failedCount, existingCount, duration);
                    });
                }

                @Override
                public void onError(String message) {
                    SwingUtilities.invokeLater(() -> {
                        addLog("【错误】" + message + " [" + getCurrentTime() + "]");
                    });
                }
            });
        } else {
            downloadService.downloadAll(shareIds, allItems, new DownloadService.DownloadProgressCallback() {
                @Override
                public void onProgress(DownloadService.DownloadProgress progress) {
                    SwingUtilities.invokeLater(() -> {
                        addOrUpdateTableRow(progress);
                    });
                }

                @Override
                public void onComplete(int total, int success, int failed, int existing, long duration) {
                    SwingUtilities.invokeLater(() -> {
                        successCount += success;
                        failedCount += failed;
                        existingCount += existing;
                        updateTotalProgress();

                        addLog("下载完成 - 总数: " + total + ", 成功: " + success + ", 失败: " + failed + ", 已存在: " + existing + " [" + getCurrentTime() + "]");

                        startDownloadButton.setEnabled(true);
                        stopDownloadButton.setEnabled(false);
                        
                        currentFileProgressBar.setValue(0);
                        currentFileProgressBar.setString("当前文件: 等待中");
                        currentFileLabel.setText("当前文件: 无");

                        showCompleteDialog(totalFiles, successCount, failedCount, existingCount, duration);
                    });
                }

                @Override
                public void onError(String message) {
                    SwingUtilities.invokeLater(() -> {
                        addLog("【错误】" + message + " [" + getCurrentTime() + "]");
                    });
                }
            });
        }
    }
    
    private void downloadSelectedFiles(boolean isBatch) {
        int[] selectedRows = fileListTable.getSelectedRows();
        
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "请选择要下载的文件", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (isBatch) {
            addLog("开始批量下载 " + selectedRows.length + " 个文件 [" + getCurrentTime() + "]");
        } else {
            addLog("开始单独下载 " + selectedRows.length + " 个文件 [" + getCurrentTime() + "]");
        }
        
        java.util.List<String> shareIds = new java.util.ArrayList<>();
        java.util.List<com.lingshu.ima.dto.helper.KnowledgeItem> items = new java.util.ArrayList<>();
        
        for (int row : selectedRows) {
            String jumpUrl = (String) fileListModel.getValueAt(row, 8);
            String shareId = (String) fileListModel.getValueAt(row, 9);
            String filePath = (String) fileListModel.getValueAt(row, 2);
            
            if (shareId != null && !shareIds.contains(shareId)) {
                shareIds.add(shareId);
            }
            
            com.lingshu.ima.dto.helper.KnowledgeItem item = new com.lingshu.ima.dto.helper.KnowledgeItem();
            item.setTitle((String) fileListModel.getValueAt(row, 1));
            item.setFileSize(String.valueOf(parseFileSize((String) fileListModel.getValueAt(row, 6))));
            item.setJumpUrl(jumpUrl);
            item.setSourcePath(filePath);
            items.add(item);
        }
        
        if (shareIds.isEmpty()) {
            addLog("【错误】无法提取ShareID，请检查下载链接是否有效");
            return;
        }
        
        addLog("提取到 " + shareIds.size() + " 个ShareID [" + getCurrentTime() + "]");
        
        downloadService.downloadAll(shareIds, items, new DownloadService.DownloadProgressCallback() {
            @Override
            public void onProgress(DownloadService.DownloadProgress progress) {
                SwingUtilities.invokeLater(() -> {
                    addOrUpdateTableRow(progress);
                });
            }
            
            @Override
            public void onComplete(int total, int success, int failed, int existing, long duration) {
                SwingUtilities.invokeLater(() -> {
                    successCount += success;
                    failedCount += failed;
                    existingCount += existing;
                    updateTotalProgress();
                    
                    addLog("下载完成 - 总数: " + total + ", 成功: " + success + ", 失败: " + failed + ", 已存在: " + existing + " [" + getCurrentTime() + "]");
                });
            }
            
            @Override
            public void onError(String message) {
                SwingUtilities.invokeLater(() -> {
                    addLog("【错误】" + message + " [" + getCurrentTime() + "]");
                });
            }
        });
    }
    
    private void copySelectedUrls() {
        int[] selectedRows = fileListTable.getSelectedRows();

        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "请选择要复制的文件", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder urls = new StringBuilder();
        for (int row : selectedRows) {
            String jumpUrl = (String) fileListModel.getValueAt(row, 8);
            if (jumpUrl != null && !jumpUrl.isEmpty()) {
                urls.append(jumpUrl).append("\n");
            }
        }

        if (urls.length() > 0) {
            java.awt.datatransfer.StringSelection selection = new java.awt.datatransfer.StringSelection(urls.toString());
            java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);

            addLog("已复制 " + selectedRows.length + " 个下载链接到剪贴板 [" + getCurrentTime() + "]");
            JOptionPane.showMessageDialog(this, "已复制 " + selectedRows.length + " 个下载链接到剪贴板", "复制成功", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "所选文件没有下载链接", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showFolderPopupMenu(MouseEvent e, JTable table, DefaultTableModel model) {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem copyUrlItem = new JMenuItem("复制下载链接");
        copyUrlItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String jumpUrl = (String) model.getValueAt(selectedRow, 5);
                    if (jumpUrl != null && !jumpUrl.isEmpty()) {
                        java.awt.datatransfer.StringSelection selection = new java.awt.datatransfer.StringSelection(jumpUrl);
                        java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, selection);
                        addLog("已复制下载链接到剪贴板 [" + getCurrentTime() + "]");
                        JOptionPane.showMessageDialog(null, "已复制下载链接到剪贴板", "复制成功", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "所选文件没有下载链接", "提示", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });

        JMenuItem copyAllLinksItem = new JMenuItem("复制文件夹所有链接");
        copyAllLinksItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyFolderAllLinks(model, "当前文件夹");
            }
        });

        popupMenu.add(copyUrlItem);
        popupMenu.add(copyAllLinksItem);

        popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    private void copyFolderAllLinks(DefaultTableModel model, String folderName) {
        StringBuilder urls = new StringBuilder();
        urls.append("========== ").append(folderName).append(" 所有文件下载链接 ==========\n\n");
        urls.append("生成时间: ").append(getCurrentTime()).append("\n\n");

        for (int i = 0; i < model.getRowCount(); i++) {
            String fileType = (String) model.getValueAt(i, 0);
            String fileName = (String) model.getValueAt(i, 1);
            String filePath = (String) model.getValueAt(i, 2);
            String jumpUrl = (String) model.getValueAt(i, 5);

            if (jumpUrl != null && !jumpUrl.isEmpty()) {
                urls.append("【").append(fileType).append("】").append(fileName);
                if (filePath != null && !filePath.isEmpty()) {
                    urls.append(" (路径: ").append(filePath).append(")");
                }
                urls.append("\n");
                urls.append("链接: ").append(jumpUrl).append("\n\n");
            }
        }

        urls.append("====================================\n");

        if (urls.length() > 0) {
            java.awt.datatransfer.StringSelection selection = new java.awt.datatransfer.StringSelection(urls.toString());
            java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);

            addLog("已复制文件夹 " + folderName + " 中所有 " + model.getRowCount() + " 个文件的下载链接到剪贴板 [" + getCurrentTime() + "]");
            JOptionPane.showMessageDialog(null, "已复制文件夹中所有 " + model.getRowCount() + " 个文件的下载链接到剪贴板", "复制成功", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "文件夹中没有可复制的下载链接", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void downloadFolderFiles(java.util.List<com.lingshu.ima.dto.helper.KnowledgeItem> items, String shareId, JDialog dialog) {
        int confirm = JOptionPane.showConfirmDialog(
            dialog,
            "确定要下载此文件夹及其所有内容吗？",
            "确认下载",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        addLog("开始使用API递归下载文件夹内容 [" + getCurrentTime() + "]");
        
        fileListModel.setRowCount(0);
        totalFiles = 0;
        successCount = 0;
        failedCount = 0;
        existingCount = 0;
        
        startDownloadButton.setEnabled(false);
        stopDownloadButton.setEnabled(true);
        
        currentFileProgressBar.setValue(0);
        currentFileProgressBar.setString("当前文件: 等待中");
        currentFileLabel.setText("当前文件: 无");
        
        downloadService.downloadAll(java.util.Collections.singletonList(shareId), null, new DownloadService.DownloadProgressCallback() {
            @Override
            public void onProgress(DownloadService.DownloadProgress progress) {
                SwingUtilities.invokeLater(() -> {
                    addOrUpdateTableRow(progress);
                });
            }
            
            @Override
            public void onComplete(int total, int success, int failed, int existing, long duration) {
                SwingUtilities.invokeLater(() -> {
                    successCount = success;
                    failedCount = failed;
                    existingCount = existing;
                    updateTotalProgress();
                    
                    addLog("文件夹下载完成 - 总数: " + total + ", 成功: " + success + ", 失败: " + failed + ", 已存在: " + existing + " [" + getCurrentTime() + "]");
                    JOptionPane.showMessageDialog(null, "文件夹下载完成！\n\n总文件数: " + total + "\n成功: " + success + "\n失败: " + failed + "\n已存在: " + existing, "下载完成", JOptionPane.INFORMATION_MESSAGE);
                    startDownloadButton.setEnabled(true);
                    stopDownloadButton.setEnabled(false);
                    
                    currentFileProgressBar.setValue(0);
                    currentFileProgressBar.setString("当前文件: 等待中");
                    currentFileLabel.setText("当前文件: 无");
                });
            }
            
            @Override
            public void onError(String message) {
                SwingUtilities.invokeLater(() -> {
                    addLog("【错误】" + message + " [" + getCurrentTime() + "]");
                });
            }
        });
        
        dialog.dispose();
    }
    
    private void stopDownload() {
        downloadService.stop();
        addLog("停止下载 [" + getCurrentTime() + "]");
        startDownloadButton.setEnabled(true);
        stopDownloadButton.setEnabled(false);
    }
    
    private void openDownloadFolder() {
        String downloadDir = config.getDownloadDir();
        File dir = new File(downloadDir);
        
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        try {
            Desktop.getDesktop().open(dir);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "无法打开文件夹: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearLog() {
        logTextArea.setText("");
        addLog("日志已清空 [" + getCurrentTime() + "]");
    }
    
    private void addOrUpdateTableRow(DownloadService.DownloadProgress progress) {
        int rowIndex = findTableRow(progress.getFileName());
        
        if (rowIndex == -1) {
            fileListModel.addRow(new Object[]{
                progress.getFileType(),
                progress.getFileName(),
                "",
                progress.getProgress(),
                formatSpeed(progress.getSpeed()),
                formatFileSize(progress.getFileSize()),
                progress.getPermission(),
                formatDuration(progress.getDuration()),
                "",
                ""
            });
        } else {
            fileListModel.setValueAt(progress.getProgress(), rowIndex, 3);
            fileListModel.setValueAt(formatSpeed(progress.getSpeed()), rowIndex, 4);
            fileListModel.setValueAt(formatFileSize(progress.getFileSize()), rowIndex, 5);
            fileListModel.setValueAt(formatDuration(progress.getDuration()), rowIndex, 7);
        }
        
        if (progress.getProgress() == 100) {
            if ("已存在".equals(progress.getStatus())) {
                existingCount++;
            } else {
                successCount++;
            }
        }
        
        updateTotalProgress();
        updateCurrentFileProgress(progress);
    }
    
    private void updateTableRow(DownloadService.DownloadProgress progress) {
        int rowIndex = findTableRow(progress.getFileName());
        
        if (rowIndex != -1) {
            fileListModel.setValueAt(progress.getStatus(), rowIndex, 3);
            fileListModel.setValueAt(progress.getProgress(), rowIndex, 4);
            fileListModel.setValueAt(formatSpeed(progress.getSpeed()), rowIndex, 5);
            fileListModel.setValueAt(formatFileSize(progress.getFileSize()), rowIndex, 6);
            fileListModel.setValueAt(formatDuration(progress.getDuration()), rowIndex, 8);
            
            if (progress.getProgress() == 100) {
                updateTotalProgress();
            }
            
            updateCurrentFileProgress(progress);
        }
    }
    
    private int findTableRow(String fileName) {
        for (int i = 0; i < fileListModel.getRowCount(); i++) {
            if (fileListModel.getValueAt(i, 1).equals(fileName)) {
                return i;
            }
        }
        return -1;
    }
    
    private void updateTotalProgress() {
        int total = totalFiles;
        int completed = successCount + existingCount;
        int progress = total > 0 ? (completed * 100) / total : 0;
        
        totalProgressBar.setValue(progress);
        totalProgressBar.setString("总进度: " + progress + "%");
        totalProgressLabel.setText("共 " + total + " 个，已完成 " + completed + " 个，失败 " + failedCount + " 个");
    }
    
    private void updateCurrentFileProgress(DownloadService.DownloadProgress progress) {
        if (progress.getProgress() > 0 && progress.getProgress() < 100) {
            currentFileProgressBar.setValue(progress.getProgress());
            currentFileProgressBar.setString(progress.getFileName() + " - " + progress.getProgress() + "%");
            currentFileLabel.setText("当前文件: " + progress.getFileName() + " (" + formatFileSize(progress.getDownloadedBytes()) + "/" + formatFileSize(progress.getFileSize()) + ")");
        } else if (progress.getProgress() == 100) {
            currentFileProgressBar.setValue(100);
            currentFileProgressBar.setString(progress.getFileName() + " - 完成");
            currentFileLabel.setText("当前文件: " + progress.getFileName() + " - 完成");
        }
    }
    
    private void showCompleteDialog(int total, int success, int failed, int existing, long duration) {
        String message = String.format(
            "下载完成！\n\n" +
            "总文件数: %d\n" +
            "成功: %d\n" +
            "失败: %d\n" +
            "已存在: %d\n" +
            "总用时: %s",
            total, success, failed, existing, formatDuration(duration)
        );
        
        Object[] options = {"打开下载文件夹", "关闭"};
        int choice = JOptionPane.showOptionDialog(
            this,
            message,
            "下载完成",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            openDownloadFolder();
        }
    }
    
    private void addLog(String message) {
        logTextArea.append(message + "\n");
        
        String[] lines = logTextArea.getText().split("\n");
        if (lines.length > 500) {
            StringBuilder sb = new StringBuilder();
            for (int i = lines.length - 500; i < lines.length; i++) {
                sb.append(lines[i]).append("\n");
            }
            logTextArea.setText(sb.toString());
        }
        
        logScrollPane.getVerticalScrollBar().setValue(logScrollPane.getVerticalScrollBar().getMaximum());
    }
    
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
    
    private String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }
    
    private String formatSpeed(long bytesPerSecond) {
        if (bytesPerSecond <= 0) {
            return "0 KB/s";
        }
        return formatFileSize(bytesPerSecond) + "/s";
    }
    
    private String formatDuration(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes % 60, seconds % 60);
        } else {
            return String.format("%d:%02d", minutes, seconds % 60);
        }
    }
    
    private String getFileType(com.lingshu.ima.dto.helper.KnowledgeItem item) {
        if (item.getMediaTypeInfo() != null && item.getMediaTypeInfo().getName() != null) {
            return item.getMediaTypeInfo().getName();
        }
        
        String mediaType = item.getMediaType();
        if (mediaType == null) {
            return "未知";
        }
        
        switch (mediaType) {
            case "1":
                return "图片";
            case "2":
                return "视频";
            case "3":
                return "文档";
            case "4":
                return "音频";
            case "5":
                return "表格";
            case "6":
                return "PPT";
            case "7":
                return "PDF";
            case "8":
                return "压缩包";
            case "9":
                return "图片";
            case "99":
                return "文件夹";
            default:
                return "其他";
        }
    }
    
    private String getPermission(com.lingshu.ima.dto.helper.KnowledgeItem item) {
        int accessStatus = item.getAccessStatus();
        switch (accessStatus) {
            case 1:
                return "可查看";
            case 2:
                return "可下载";
            case 3:
                return "完全访问";
            default:
                return "未知";
        }
    }
    
    private String extractShareIdFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        
        String pattern = "shareId[=]([a-fA-F0-9]{64})";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(url);
        
        if (m.find()) {
            return m.group(1);
        }
        
        return null;
    }
    
    private long parseFileSize(String fileSizeStr) {
        try {
            return Long.parseLong(fileSizeStr);
        } catch (Exception e) {
            return 0L;
        }
    }
    
    private void pasteAndDownloadLinks() {
        String clipboardText = getClipboardText();
        
        if (clipboardText == null || clipboardText.trim().isEmpty()) {
            addLog("【提示】剪贴板为空或只包含空白字符 [" + getCurrentTime() + "]");
            JOptionPane.showMessageDialog(this, "剪贴板为空或只包含空白字符", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        pasteTextArea.setText(clipboardText);
        addLog("已将剪贴板内容粘贴到文本框 [" + getCurrentTime() + "]");
        addLog("剪贴板内容长度: " + clipboardText.length() + " 字符");
        
        java.util.List<PasteFileInfo> fileInfos = extractDownloadLinks(clipboardText);
        
        if (fileInfos.isEmpty()) {
            addLog("【提示】未找到任何下载链接 [" + getCurrentTime() + "]");
            JOptionPane.showMessageDialog(this, "未找到任何下载链接", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        addLog("识别到 " + fileInfos.size() + " 个下载链接 [" + getCurrentTime() + "]");
        
        addLog("开始批量下载 " + fileInfos.size() + " 个文件，使用粘贴的下载链接 [" + getCurrentTime() + "]");
        
        fileListModel.setRowCount(0);
        totalFiles = 0;
        successCount = 0;
        failedCount = 0;
        existingCount = 0;
        
        java.util.List<com.lingshu.ima.dto.helper.KnowledgeItem> items = new java.util.ArrayList<>();
        
        for (PasteFileInfo fileInfo : fileInfos) {
            com.lingshu.ima.dto.helper.KnowledgeItem item = new com.lingshu.ima.dto.helper.KnowledgeItem();
            item.setTitle(fileInfo.fileName);
            item.setJumpUrl(fileInfo.downloadUrl);
            item.setSourcePath(fileInfo.filePath);
            item.setFileSize("0");
            items.add(item);
            
            String fileType = "文档";
            if (fileInfo.fileName.toLowerCase().endsWith(".pdf")) {
                fileType = "PDF";
            } else if (fileInfo.fileName.toLowerCase().endsWith(".doc") || fileInfo.fileName.toLowerCase().endsWith(".docx")) {
                fileType = "文档";
            } else if (fileInfo.fileName.toLowerCase().endsWith(".xls") || fileInfo.fileName.toLowerCase().endsWith(".xlsx")) {
                fileType = "表格";
            } else if (fileInfo.fileName.toLowerCase().endsWith(".ppt") || fileInfo.fileName.toLowerCase().endsWith(".pptx")) {
                fileType = "PPT";
            } else if (fileInfo.fileName.toLowerCase().endsWith(".jpg") || fileInfo.fileName.toLowerCase().endsWith(".png") || fileInfo.fileName.toLowerCase().endsWith(".gif")) {
                fileType = "图片";
            } else if (fileInfo.fileName.toLowerCase().endsWith(".mp4") || fileInfo.fileName.toLowerCase().endsWith(".avi") || fileInfo.fileName.toLowerCase().endsWith(".mkv")) {
                fileType = "视频";
            } else if (fileInfo.fileName.toLowerCase().endsWith(".mp3") || fileInfo.fileName.toLowerCase().endsWith(".wav")) {
                fileType = "音频";
            } else if (fileInfo.fileName.toLowerCase().endsWith(".zip") || fileInfo.fileName.toLowerCase().endsWith(".rar")) {
                fileType = "压缩包";
            }
            
            fileListModel.addRow(new Object[]{fileType, fileInfo.fileName, fileInfo.filePath, 0, "0 KB/s", "0 B", "可下载", "0:00", fileInfo.downloadUrl, "pasted"});
            totalFiles++;
        }
        
        startDownloadButton.setEnabled(false);
        stopDownloadButton.setEnabled(true);
        
        currentFileProgressBar.setValue(0);
        currentFileProgressBar.setString("当前文件: 等待中");
        currentFileLabel.setText("当前文件: 无");
        
        downloadService.downloadFastMode(java.util.Collections.singletonList("pasted"), items, new DownloadService.DownloadProgressCallback() {
            @Override
            public void onProgress(DownloadService.DownloadProgress progress) {
                SwingUtilities.invokeLater(() -> {
                    addOrUpdateTableRow(progress);
                });
            }
            
            @Override
            public void onComplete(int total, int success, int failed, int existing, long duration) {
                SwingUtilities.invokeLater(() -> {
                    successCount = success;
                    failedCount = failed;
                    existingCount = existing;
                    updateTotalProgress();
                    
                    addLog("粘贴下载完成 - 总数: " + total + ", 成功: " + success + ", 失败: " + failed + ", 已存在: " + existing + " [" + getCurrentTime() + "]");
                    JOptionPane.showMessageDialog(null, "粘贴下载完成！\n\n总文件数: " + total + "\n成功: " + success + "\n失败: " + failed + "\n已存在: " + existing, "下载完成", JOptionPane.INFORMATION_MESSAGE);
                    startDownloadButton.setEnabled(true);
                    stopDownloadButton.setEnabled(false);
                    
                    currentFileProgressBar.setValue(0);
                    currentFileProgressBar.setString("当前文件: 等待中");
                    currentFileLabel.setText("当前文件: 无");
                });
            }
            
            @Override
            public void onError(String message) {
                SwingUtilities.invokeLater(() -> {
                    addLog("【错误】" + message + " [" + getCurrentTime() + "]");
                });
            }
        });
    }
    
    private String getClipboardText() {
        try {
            java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
            java.awt.datatransfer.Transferable contents = clipboard.getContents(null);
            
            if (contents == null) {
                return null;
            }
            
            if (contents.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.stringFlavor)) {
                return (String) contents.getTransferData(java.awt.datatransfer.DataFlavor.stringFlavor);
            }
            
            return null;
        } catch (Exception e) {
            addLog("【错误】获取剪贴板内容失败: " + e.getMessage() + " [" + getCurrentTime() + "]");
            return null;
        }
    }
    
    private java.util.List<PasteFileInfo> extractDownloadLinks(String text) {
        java.util.List<PasteFileInfo> fileInfos = new java.util.ArrayList<>();
        
        String[] lines = text.split("\\r?\\n");
        String currentFileName = null;
        String currentFilePath = null;
        String currentDownloadUrl = null;
        
        addLog("开始解析粘贴内容，共 " + lines.length + " 行 [" + getCurrentTime() + "]");
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            
            if (line.isEmpty()) {
                continue;
            }
            
            if (line.startsWith("==========") || line.startsWith("生成时间") || line.startsWith("====================================")) {
                continue;
            }
            
            if (line.startsWith("【") && line.contains("】")) {
                if (currentFileName != null && currentDownloadUrl != null) {
                    fileInfos.add(new PasteFileInfo(currentFileName, currentFilePath, currentDownloadUrl));
                    addLog("提取文件: " + currentFileName + ", 路径: " + currentFilePath);
                }
                
                int endBracket = line.indexOf("】");
                String rest = line.substring(endBracket + 1).trim();
                
                if (rest.contains(" (路径: ")) {
                    int pathMarker = rest.indexOf(" (路径: ");
                    currentFileName = rest.substring(0, pathMarker).trim();
                    
                    int pathStart = pathMarker + 7;
                    int pathEnd = rest.indexOf(")", pathStart);
                    if (pathEnd > pathStart) {
                        currentFilePath = rest.substring(pathStart, pathEnd).trim();
                    } else {
                        currentFilePath = "";
                    }
                } else {
                    currentFileName = rest.trim();
                    currentFilePath = "";
                }
                
                currentDownloadUrl = null;
            } else if (line.startsWith("链接: `") && line.endsWith("`")) {
                currentDownloadUrl = line.substring(5, line.length() - 1).trim();
                addLog("提取链接: " + currentDownloadUrl.substring(0, Math.min(50, currentDownloadUrl.length())) + "...");
            } else if (line.startsWith("链接: ") && !line.startsWith("链接: `")) {
                currentDownloadUrl = line.substring(4).trim();
                addLog("提取链接: " + currentDownloadUrl.substring(0, Math.min(50, currentDownloadUrl.length())) + "...");
            }
        }
        
        if (currentFileName != null && currentDownloadUrl != null) {
            fileInfos.add(new PasteFileInfo(currentFileName, currentFilePath, currentDownloadUrl));
            addLog("提取文件: " + currentFileName + ", 路径: " + currentFilePath);
        }
        
        addLog("解析完成，共提取 " + fileInfos.size() + " 个文件信息 [" + getCurrentTime() + "]");
        
        return fileInfos;
    }
    
    private String extractFileNameFromUrl(String url) {
        try {
            java.net.URI uri = new java.net.URI(url);
            String path = uri.getPath();
            
            if (path == null || path.isEmpty()) {
                return "未知文件_" + System.currentTimeMillis();
            }
            
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            
            if (fileName.isEmpty()) {
                return "未知文件_" + System.currentTimeMillis();
            }
            
            String decodedFileName = java.net.URLDecoder.decode(fileName, "UTF-8");
            
            String extension = getFileExtension(fileName);
            if (extension.isEmpty()) {
                extension = getFileExtensionFromQuery(uri.getQuery());
            }
            
            if (!extension.isEmpty() && !decodedFileName.toLowerCase().endsWith(extension.toLowerCase())) {
                decodedFileName += extension;
            }
            
            String sanitized = sanitizeFileName(decodedFileName);
            
            if (sanitized.isEmpty() || sanitized.length() < 3) {
                return "文件_" + System.currentTimeMillis();
            }
            
            return sanitized;
        } catch (Exception e) {
            return "文件_" + System.currentTimeMillis();
        }
    }
    
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(lastDot);
        }
        return "";
    }
    
    private String getFileExtensionFromQuery(String query) {
        if (query == null || query.isEmpty()) {
            return "";
        }
        
        try {
            java.util.Map<String, String> params = new java.util.HashMap<>();
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=", 2);
                if (kv.length == 2) {
                    params.put(kv[0], kv[1]);
                }
            }
            
            String type = params.get("type");
            if (type != null && !type.isEmpty()) {
                String decoded = java.net.URLDecoder.decode(type, "UTF-8");
                if (!decoded.startsWith(".")) {
                    decoded = "." + decoded;
                }
                return decoded;
            }
        } catch (Exception e) {
            // 忽略异常
        }
        
        return "";
    }
    
    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "unknown_file";
        }
        
        String sanitized = fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
        sanitized = sanitized.replaceAll("\\s+", "_");
        sanitized = sanitized.replaceAll("[\\x00-\\x1F\\x7F]", "");
        
        if (sanitized.length() > 200) {
            String ext = getFileExtension(sanitized);
            String nameWithoutExt = sanitized.substring(0, sanitized.length() - ext.length());
            nameWithoutExt = nameWithoutExt.substring(0, 200 - ext.length());
            sanitized = nameWithoutExt + ext;
        }
        
        return sanitized;
    }
    
    private void showUsageDialog() {
        String usageText = "灵枢IMA智能下载器 V2.2 使用说明\n\n" +
            "【功能介绍】\n" +
            "1. 标准下载模式：通过ShareID或URL获取文件列表，支持文件夹递归下载\n" +
            "2. 快速下载模式：直接使用下载链接，跳过API验证\n" +
            "3. 粘贴下载：支持从剪贴板粘贴文件信息并批量下载\n\n" +
            "【粘贴格式】\n" +
            "支持以下两种格式：\n\n" +
            "格式1（包含文件名和路径）：\n" +
            "【文件类型】文件名 (路径: 文件夹路径)\n" +
            "链接: `下载链接`\n\n" +
            "格式2（仅URL）：\n" +
            "直接粘贴myqcloud.com的下载链接\n\n" +
            "【操作说明】\n" +
            "1. 输入ShareID或URL到输入框，点击\"读取文件列表\"\n" +
            "2. 查看文件列表，双击文件夹可查看内容\n" +
            "3. 点击\"开始下载\"开始下载，或使用\"粘贴并下载\"功能\n" +
            "4. 点击\"停止下载\"可随时停止下载任务\n" +
            "5. 文件将按照文件夹结构保存到下载目录\n\n" +
            "【注意事项】\n" +
            "- 下载前请确保网络连接正常\n" +
            "- 大文件下载可能需要较长时间\n" +
            "- 文件夹会自动递归下载所有内容\n" +
            "- 已存在的文件会自动跳过\n" +
            "- 快速下载模式默认开启\n" +
            "【版权声明】\n" +
            "本软件由作者:灵枢 开发，仅供学习交流使用\n" +
            "严禁任何形式的非法破解、反编译、逆向工程等行为\n" +
            "使用者应当遵守相关法律法规，尊重知识产权\n" +
            "作者:灵枢 保留所有权利\n\n" +
            "【使用条款】\n" +
            "1. 本软件仅供学习交流使用，不得用于任何商业目的。\n" +
            "2. 使用者应当遵守相关法律法规，尊重知识产权。\n" +
            "3. 严禁使用本软件下载或传播任何违法违规内容。\n" +
            "4. 使用者应当自行承担使用本软件的风险和后果。\n\n" +
            "【免责声明】\n" +
            "1. 本软件及作者仅提供为正常下载的便利服务，不保证其完整性、可靠性和安全性，不对使用本软件造成的任何直接或间接损失承担责任，使用者应当自行承担使用本软件的风险和后果。\n" +
            "2. 本软件可能会因网络环境、系统配置等因素影响使用效果如遇问题请优先重启解决。";
        
        JTextArea textArea = new JTextArea(usageText);
        textArea.setEditable(false);
        textArea.setFont(new java.awt.Font("微软雅黑", java.awt.Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(600, 550));
        
        int result = JOptionPane.showConfirmDialog(this, scrollPane, "使用说明", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            System.exit(0);
        }
    }
    
    private static class ProgressBarRenderer extends JProgressBar implements javax.swing.table.TableCellRenderer {
        @Override
        public java.awt.Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            
            int progress = 0;
            if (value instanceof Integer) {
                progress = (Integer) value;
            }
            
            setValue(progress);
            setStringPainted(true);
            setString(progress + "%");
            
            if (progress == 100) {
                setForeground(new Color(0, 150, 0));
            } else {
                setForeground(new Color(0, 120, 215));
            }
            
            return this;
        }
    }
    
    //版权声明
    //本软件由作者:灵枢 开发，仅供学习交流使用
    //严禁任何形式的非法破解、反编译、逆向工程等行为
    //使用者应当遵守相关法律法规，尊重知识产权
    //作者:灵枢 保留所有权利
}
