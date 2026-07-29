//版权声明
//本软件由作者:灵枢 开发，仅供学习交流使用
//严禁任何形式的非法破解、反编译、逆向工程等行为
//使用者应当遵守相关法律法规，尊重知识产权
//作者:灵枢 保留所有权利

package com.lingshu.ima;

import com.lingshu.ima.ui.UnifiedMainFrame;

import javax.swing.*;

public class UnifiedMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            setApplicationIcon();

            UnifiedMainFrame frame = new UnifiedMainFrame();
            java.awt.Image appIcon = loadIcon();
            if (appIcon != null) frame.setIconImage(appIcon);
            frame.setVisible(true);
        });
    }

    private static java.awt.Image loadIcon() {
        try {
            java.net.URL iconUrl = UnifiedMain.class.getClassLoader().getResource("icon/icon_256.png");
            if (iconUrl != null) {
                return new ImageIcon(iconUrl).getImage();
            }
        } catch (Exception e) {}
        return null;
    }

    private static void setApplicationIcon() {
        try {
            java.net.URL iconUrl = UnifiedMain.class.getClassLoader().getResource("icon/icon_256.png");
            if (iconUrl != null) {
                java.awt.Image icon = new ImageIcon(iconUrl).getImage();
                if (java.awt.Taskbar.isTaskbarSupported()) {
                    java.awt.Taskbar.getTaskbar().setIconImage(icon);
                }
            }
        } catch (Exception e) {
            // ignore icon errors on unsupported platforms
        }
    }
}
