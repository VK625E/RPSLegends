/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package rpslegends;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.ResultSet;
import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author SAu
 */
public class Game extends javax.swing.JFrame {

    private int userId;
    private String userName;
    private int win = 0;

public Game(int userId, String userName) {
    initComponents();
    this.userId = userId;
    this.userName = userName;
    Logging(0);
}
    
    String GLPlayerChoice;
    String GLCPUChoice;
    int health = 3;
    int score = 0;
    boolean highScore = true;
        
    public Game() {
        initComponents();
        BtnGo.setEnabled(false);
        
    }
    
    private void CPUChoice() {
        String[] choices = {"Rock","Paper","Scissors"};
        Random rand = new Random();
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        int index = rand.nextInt(choices.length);
        String CPUChoice = choices[index];
        String imagePath = "src/assets/images/" + CPUChoice + ".png";
        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image originalImage = originalIcon.getImage();
        int width = 100;
        int height = 100;
        Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        ImgCPU.setIcon(new ImageIcon(resizedImage));
        ImgCPU.repaint();
        
        System.out.println("CPU chose: " + CPUChoice);
        GLCPUChoice = CPUChoice;
        Scoring();
    }

    private void Scoring(){
        System.out.println("Scoring running");
        String result;
        String playerChoice = GLPlayerChoice;
        String CPUChoice = GLCPUChoice;
        int HealthBefore = health;
        int ScoreBefore = score;
        
        if (playerChoice.equals(CPUChoice)) {
        result = "Draw!";
        } 
        else if ((playerChoice.equals("Rock") && CPUChoice.equals("Scissors")) ||
                (playerChoice.equals("Scissors") && CPUChoice.equals("Paper")) ||
                (playerChoice.equals("Paper") && CPUChoice.equals("Rock"))) {
            result = "You win!";
            score++;
            System.out.println("Player wins");
        } else {
            result = "You lost!";
            health--;
            System.out.println("Player lost");
            if (health <= 0) {
                result = "Game Over!";
                BtnRock.setEnabled(false);
                BtnPaper.setEnabled(false);
                BtnScissors.setEnabled(false);
                BtnGo.setFocusable(false);
                BtnGo.setEnabled(false);
                
            }
        }
        
        Announcer.setText(result);
        LabelHealth.setText("Health: " + health + "/3");
        LabelScore.setText("SCORE: " + score);
        System.out.println("Health: " + HealthBefore + " -> " + health);
        System.out.println("Score: " + ScoreBefore + " -> " + score);
        Logging(1);
        if (health <= 0) {
            System.out.println("Game over");
            NameFrame();
            Logging(2);
        }
    }
    
    public void NameFrame() {
    String userName = JOptionPane.showInputDialog(this, "Enter your name.");
    if (userName != null && !userName.isEmpty()) {
        updateNamaPengguna(userName);
        simpanLeaderboard(score);
        Leaderboard obj = new Leaderboard();
        obj.setVisible(true);
        this.dispose();
    } else {
        TitleScreen obj = new TitleScreen();
        obj.setVisible(true);
        this.dispose();
    }
    }
    
    private void Logging(int act) {
        String caseAction;
        switch (act) {
            case 0:
                caseAction = "Game Start";
                break;
            case 1:
                caseAction = GLPlayerChoice + " VS " + GLCPUChoice;
                break;
            case 2:
                caseAction = "Game over";
                break;
            default:
                caseAction = "Undocumented";
                break;
        }
        try {
            Connection conn = DBConnect.getConnection();
            if (conn != null) {
                try {
                    String query = "INSERT INTO logs (id_user, action, time_stamp) VALUES (?, ?, NOW())";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, userId);
                    pstmt.setString(2, caseAction);
                    pstmt.executeUpdate();
                    pstmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void updateNamaPengguna(String userName) {
        checkExistingUser(userName);
        try {
            Connection conn = DBConnect.getConnection();
            if (conn != null) {
               try {
                    if (userName.length() > 20) {
                        userName = userName.substring(0, 20);
                    }
                    String query = "UPDATE users SET user_name = ? WHERE id_user = ?";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, userName);
                    pstmt.setInt(2, userId);
                    pstmt.executeUpdate();
                    pstmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void checkExistingUser(String userName) {
        try {
            Connection conn = DBConnect.getConnection();
            if (conn != null) {
                try {
                    String query = "SELECT id_user FROM users WHERE user_name = ?";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, userName);
                    ResultSet rs = pstmt.executeQuery();
                    
                    if (rs.next()) {
                        int getUserId = rs.getInt("id_user");
                        rs.close();
                        pstmt.close();
                        conn.close();
                        compareScores(getUserId);
                    }
                    
                    rs.close();
                    pstmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void compareScores(int getUserId) { 
        try {
            Connection conn = DBConnect.getConnection();
            if (conn != null) {
                try {
                    String query = "SELECT score FROM leaderboard WHERE id_user = ?";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, getUserId);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) { 
                        int existingScore = rs.getInt("score");
                        if (score > existingScore) {
                            userId = getUserId;
                        } else {
                           highScore = false;
                        }
                    }
                    rs.close();
                    pstmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void simpanLeaderboard(int score) {
        try {
            Connection conn = DBConnect.getConnection();
            if (conn != null) {
                try {
                    if (cekIdUserDiLeaderboard(userId)) {
                        updateLeaderboard(score);
                    } else if (highScore) {
                        String query = "INSERT INTO leaderboard (id_user, score) VALUES (?, ?)";
                        PreparedStatement pstmt = conn.prepareStatement(query);
                        pstmt.setInt(1, userId);
                        pstmt.setInt(2, score);
                        pstmt.executeUpdate();
                        pstmt.close();
                    } else {
                        System.out.println("Error: Unknown action on leaderboard"); 
                    }
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean cekIdUserDiLeaderboard(int userId) {
        try {
            Connection conn = DBConnect.getConnection();
            if (conn != null) {
                try {
                    String query = "SELECT id_user FROM leaderboard WHERE id_user = ?";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, userId);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        return true;
                    }
                    rs.close();
                    pstmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }


    private void updateLeaderboard(int score) {
        try {
            Connection conn = DBConnect.getConnection();
            if (conn != null) {
                try {
                    String query = "UPDATE leaderboard SET score = ? WHERE id_user = ?";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, score);
                    pstmt.setInt(2, userId);
                    pstmt.executeUpdate();
                    pstmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    private void SelectChoice(String choice){
        if (choice != null) {
            String imagePath = "src/assets/images/" + choice + ".png";
        
            ImageIcon oriIcon = new ImageIcon(imagePath);
            Image oriImg = oriIcon.getImage();
            int width = 100;
            int height = 100;
            Image resizedImage = oriImg.getScaledInstance(width, height, Image.SCALE_SMOOTH);

            ImgPlayer.setIcon(new ImageIcon(resizedImage));
            ImgPlayer.repaint();
            
            System.out.println("Player chose: " + choice);
            GLPlayerChoice = choice;
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

        LabelHealth = new javax.swing.JLabel();
        LabelScore = new javax.swing.JLabel();
        BtnScissors = new javax.swing.JButton();
        BtnRock = new javax.swing.JButton();
        BtnPaper = new javax.swing.JButton();
        BtnGo = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        Announcer = new javax.swing.JLabel();
        ImgPlayer = new javax.swing.JLabel();
        ImgCPU = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        LabelHealth.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        LabelHealth.setText("Health: 3/3");

        LabelScore.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        LabelScore.setText("SCORE: 0");

        BtnScissors.setText("Scissors");
        BtnScissors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnScissorsActionPerformed(evt);
            }
        });

        BtnRock.setText("Rock");
        BtnRock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnRockActionPerformed(evt);
            }
        });

        BtnPaper.setText("Paper");
        BtnPaper.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPaperActionPerformed(evt);
            }
        });

        BtnGo.setText("GO");
        BtnGo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnGoActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("VS");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("You");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("CPU");

        Announcer.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N

        ImgPlayer.setText(" ");

        ImgCPU.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(50, 50, 50))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(ImgPlayer, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(Announcer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ImgCPU, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(LabelScore, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BtnGo, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(LabelHealth, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BtnRock, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BtnPaper, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BtnScissors, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                        .addComponent(Announcer))
                    .addComponent(ImgCPU, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ImgPlayer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(45, 45, 45)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnPaper, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtnScissors, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtnRock, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelHealth))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnGo, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelScore))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void BtnScissorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnScissorsActionPerformed
        // TODO add your handling code here:
        SelectChoice("Scissors");
        BtnGo.setEnabled(true);
    }//GEN-LAST:event_BtnScissorsActionPerformed

    private void BtnRockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnRockActionPerformed
        // TODO add your handling code here:
        SelectChoice("Rock");
        BtnGo.setEnabled(true);
    }//GEN-LAST:event_BtnRockActionPerformed

    private void BtnPaperActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPaperActionPerformed
        // TODO add your handling code here:
        SelectChoice("Paper");
        BtnGo.setEnabled(true);
    }//GEN-LAST:event_BtnPaperActionPerformed

    private void BtnGoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnGoActionPerformed
        // TODO add your handling code here:
        if (ImgPlayer.getIcon() != null) {
            // LOGS SCRIPT HERE | LOGS SCRIPT HERE | LOGS SCRIPT HERE | LOGS SCRIPT HERE | 
            CPUChoice();
        } else {
            System.out.println("ERROR! NO CHOICE HAS BEEN SELECTED!");
        }
    }//GEN-LAST:event_BtnGoActionPerformed

    
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
            java.util.logging.Logger.getLogger(Game.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Game.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Game.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Game.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Game().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Announcer;
    private javax.swing.JButton BtnGo;
    private javax.swing.JButton BtnPaper;
    private javax.swing.JButton BtnRock;
    private javax.swing.JButton BtnScissors;
    private javax.swing.JLabel ImgCPU;
    private javax.swing.JLabel ImgPlayer;
    private javax.swing.JLabel LabelHealth;
    private javax.swing.JLabel LabelScore;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    // End of variables declaration//GEN-END:variables
}
