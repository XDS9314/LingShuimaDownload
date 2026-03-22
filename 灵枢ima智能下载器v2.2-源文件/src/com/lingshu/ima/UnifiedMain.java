//版权声明
//本软件由作者:灵枢 开发，仅供学习交流使用
//严禁任何形式的非法破解、反编译、逆向工程等行为
//使用者应当遵守相关法律法规，尊重知识产权
//作者:灵枢 保留所有权利

package com.lingshu.ima;

import com.lingshu.ima.ui.UnifiedMainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

//版权声明
//本软件由作者:灵枢 开发，仅供学习交流使用
//严禁任何形式的非法破解、反编译、逆向工程等行为
//使用者应当遵守相关法律法规，尊重知识产权
//作者:灵枢 保留所有权利

public class UnifiedMain {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            UnifiedMainFrame frame = new UnifiedMainFrame();
            frame.setVisible(true);
        });
    }
    
    //版权声明
    //本软件由作者:灵枢 开发，仅供学习交流使用
    //严禁任何形式的非法破解、反编译、逆向工程等行为
    //使用者应当遵守相关法律法规，尊重知识产权
    //作者:灵枢 保留所有权利
}
