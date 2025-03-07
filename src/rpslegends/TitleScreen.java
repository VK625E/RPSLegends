/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package rpslegends;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author SAu
 */
public class TitleScreen extends javax.swing.JFrame {

    /**
     * Creates new form TitleScreen
     */
    public TitleScreen() {
        nullPurging();
        initComponents();
    }

    private void nullPurging() {
        try {
            Connection conn = DBConnect.getConnection();
            if (conn != null) {
                try {
                    String query = "DELETE FROM users WHERE user_name = '' OR user_name IS NULL";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.executeUpdate();
                    pstmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }   } catch (SQLException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        BtnPlay = new javax.swing.JButton();
        BtnLdb = new javax.swing.JButton();
        BtnExit = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("RPS");

        BtnPlay.setText("PLAY");
        BtnPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPlayActionPerformed(evt);
            }
        });

        BtnLdb.setText("LEADERBOARD");
        BtnLdb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnLdbActionPerformed(evt);
            }
        });

        BtnExit.setText("EXIT");
        BtnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnExitActionPerformed(evt);
            }
        });

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Arcade Edition");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(131, 131, 131)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BtnPlay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BtnLdb, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                    .addComponent(BtnExit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(130, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(43, 43, 43)
                .addComponent(BtnPlay)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BtnLdb)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BtnExit)
                .addContainerGap(52, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPlayActionPerformed
        int userId = buatAkunPengguna(""); // Membuat akun dengan nama kosong
        if (userId != -1) {
            String userName = dapatkanNamaPengguna(userId); // Mendapatkan nama pengguna dari database
            if (userName != null) {
                Game obj = new Game(userId, userName); // Kirim userId dan userName ke Game
                obj.setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error when fetching username");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error when creating account");
        }
    }//GEN-LAST:event_BtnPlayActionPerformed

    private int buatAkunPengguna(String userName) {
        try {
            Connection conn = DBConnect.getConnection();
            if (conn != null) {
                try {
                    String query = "INSERT INTO users (user_name) VALUES (?)";
                    PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                    pstmt.setString(1, userName);
                    pstmt.executeUpdate();
                    
                    ResultSet rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                        return rs.getInt(1); // Mengembalikan userId yang baru dibuat
                    }
                    
                    pstmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return -1; // Mengembalikan -1 jika gagal
        } catch (SQLException ex) {
            Logger.getLogger(TitleScreen.class.getName()).log(Level.SEVERE, null, ex);
        }     return -1; // Mengembalikan -1 jika gagal
}


private String dapatkanNamaPengguna(int userId) {
        try {
            Connection conn = DBConnect.getConnection();
            if (conn != null) {
                try {
                    String query = "SELECT user_name FROM users WHERE id_user = ?";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, userId);
                    ResultSet rs = pstmt.executeQuery();
                    
                    if (rs.next()) {
                        return rs.getString("user_name");
                    }
                    
                    pstmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return null; // Mengembalikan null jika gagal
        } catch (SQLException ex) {
            Logger.getLogger(TitleScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
     return null; // Mengembalikan -1 jika gagal
}
    
    private void BtnLdbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnLdbActionPerformed
          Leaderboard obj = new Leaderboard();
        obj.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_BtnLdbActionPerformed
private JFrame frame;
    private void BtnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnExitActionPerformed
   frame = new JFrame("Exit");
     if(JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit this game??", "MYSQL Connector",
             JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
     {
         System.exit(0);
     }
    }//GEN-LAST:event_BtnExitActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TitleScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TitleScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TitleScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TitleScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TitleScreen().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnExit;
    private javax.swing.JButton BtnLdb;
    private javax.swing.JButton BtnPlay;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}
