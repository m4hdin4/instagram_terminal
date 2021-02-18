import org.omg.CORBA.FREE_MEM;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;


public class Instagram {
    // Reset
    public static final String RESET = "\033[0m";  // Text Reset

    // Regular Colors
    public static final String BLACK = "\033[0;30m";   // BLACK
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String PURPLE = "\033[0;35m";  // PURPLE
    public static final String CYAN = "\033[0;36m";    // CYAN
    public static final String WHITE = "\033[0;37m";   // WHITE

    // Bold
    public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
    public static final String RED_BOLD = "\033[1;31m";    // RED
    public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String BLUE_BOLD = "\033[1;34m";   // BLUE
    public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    public static final String CYAN_BOLD = "\033[1;36m";   // CYAN
    public static final String WHITE_BOLD = "\033[1;37m";  // WHITE


    private String username;
    Connection con;


    private void startConnection(boolean isInstagram) {
        Scanner scanner = new Scanner(System.in);

        Connection con;
        String user;
        String password;
        if (isInstagram) {
            System.out.println(PURPLE_BOLD + "please enter you'r username:    " + RESET);
            user = scanner.nextLine();
            if (user.equals("root")) {
                System.out.println(RED_BOLD + "you must choose the other option!! :)\n" +
                        "user root is just for the DBA!" + RESET);
                con = null;
                return;
            }
            System.out.println(PURPLE_BOLD + "please enter you'r password:    " + RESET);
            password = scanner.nextLine();
        } else {
            System.out.println(PURPLE_BOLD + "please enter the system password:    " + RESET);
            String pass = scanner.nextLine();
            try {
                Connection creation = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/sys", "root", pass);
                ScriptRunner runner = new ScriptRunner(creation, false, true);
                runner.runScript(new BufferedReader(new FileReader("instagram_creation.sql")));
                System.out.println(WHITE_BOLD + "successfully created/refreshed the base schema!!" + RESET);
                creation.close();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
                con = null;
                return;
            }
            user = "instagram_admin";
            password = "pro";
        }
        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/instagram", user, password);
            username = user;
        } catch (SQLException e) {
            e.printStackTrace();
            con = null;
            return;
        }

//                Statement stmt = con2.createStatement();
//                stmt.executeUpdate("CREATE USER 'instagram_admin'@'localhost' IDENTIFIED BY 'pro';");
//        return con;
        this.con = con;
    }

    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);
        Instagram insta = new Instagram();
        boolean work = true;

        System.out.print(PURPLE_BOLD + "do you have an account?(y/Yes n/No):    " + RESET);
        String ans = scanner.next();
        ans = ans.toLowerCase();
        boolean answer = ans.equals("y") || ans.equals("ye") || ans.equals("yes");
        insta.startConnection(answer);
        if (insta.con == null) {
            System.out.println(RED_BOLD + "couldn't make the connection" + RESET);
            return;
        }
        String input;
        /* code */
        scanner.nextLine();
        System.out.println(WHITE_BOLD + "Hi " + insta.username + RESET);
        System.out.println(WHITE_BOLD + "you are connected!" + RESET);
        while (true) {
            System.out.println(WHITE_BOLD + "1.see posts" + RESET);
            System.out.println(WHITE_BOLD + "2.new post" + RESET);
            System.out.println(WHITE_BOLD + "3.followers" + RESET);
            System.out.println(WHITE_BOLD + "4.followee" + RESET);
            System.out.println(WHITE_BOLD + "5.find a friend" + RESET);
            System.out.println(WHITE_BOLD + "6.add a user" + RESET);
            System.out.println(WHITE_BOLD + "7.more details" + RESET);
            System.out.println(WHITE_BOLD + "8.exit" + RESET);

            input = scanner.nextLine();
            switch (input) {
                case "1":
                    try {
                        PreparedStatement pStatement = insta.con.prepareStatement("CALL see_posts (?)");
                        pStatement.setString(1, insta.username);
                        ResultSet rSet = pStatement.executeQuery();
                        System.out.println(YELLOW + "post_id                  user                     image_url                created_at" + RESET);
                        System.out.println(BLUE);
                        int temp2;
                        while (rSet.next()) {
                            for (int i = 1; i <= 4; i++) {
                                System.out.print(rSet.getString(i));
                                temp2 = 25 - rSet.getString(i).length();
                                for (int j = 0; j < temp2; j++) {
                                    System.out.print(" ");
                                }
                            }
                            System.out.println();
                        }
                        System.out.println(RESET);
                        pStatement.close();
                        System.out.println(WHITE_BOLD + "do you want to see one?\nenter its code(0 to exit):  " + RESET);
                        input = scanner.nextLine();
                        int temp = Integer.parseInt(input);
                        if (temp == 0)
                            break;
                        System.out.println(WHITE_BOLD + "1.like/unlike" + RESET);
                        System.out.println(WHITE_BOLD + "2.put a comment" + RESET);
                        System.out.println(WHITE_BOLD + "3.exit\n" + RESET);
                        input = scanner.nextLine();
                        while (!input.equals("3")) {
                            if (input.equals("1")) {
                                System.out.println(WHITE_BOLD + "1.like\n2.unlike" + RESET);
                                input = scanner.nextLine();
                                if (input.equals("1")) {
                                    pStatement = insta.con.prepareStatement("CALL like_posts (?,?,?)");
                                    pStatement.setString(1, insta.username);
                                    pStatement.setInt(2, temp);
                                    pStatement.setBoolean(3, true);
                                    pStatement.executeQuery();
                                    pStatement.close();
                                    System.out.println(GREEN_BOLD + "Done" + RESET);
                                } else if (input.equals("2")) {
                                    pStatement = insta.con.prepareStatement("CALL like_posts (?,?,?)");
                                    pStatement.setString(1, insta.username);
                                    pStatement.setInt(2, temp);
                                    pStatement.setBoolean(3, false);
                                    pStatement.executeQuery();
                                    pStatement.close();
                                    System.out.println(GREEN_BOLD + "Done" + RESET);
                                }
                            } else if (input.equals("2")) {
                                System.out.println(WHITE_BOLD + "please type your comment" + RESET);
                                input = scanner.nextLine();
                                pStatement = insta.con.prepareStatement("CALL comment_posts (?,?,?)");
                                pStatement.setString(1, insta.username);
                                pStatement.setInt(2, temp);
                                pStatement.setString(3, input);
                                pStatement.executeQuery();
                                pStatement.close();
                                System.out.println(GREEN_BOLD + "Done" + RESET);
                            }
                            System.out.println(WHITE_BOLD + "1.like/unlike" + RESET);
                            System.out.println(WHITE_BOLD + "2.put a comment" + RESET);
                            System.out.println(WHITE_BOLD + "3.exit" + RESET);
                            input = scanner.nextLine();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "2":
                    System.out.println(WHITE_BOLD + "please enter the photo url to download the photo:" + RESET);
                    input = scanner.nextLine();
                    try {
                        PreparedStatement pStatement = insta.con.prepareStatement("CALL add_post (?,?)");
                        pStatement.setString(1, insta.username);
                        pStatement.setString(2, input);
                        pStatement.executeQuery();
                        pStatement.close();
                        System.out.println(GREEN_BOLD + "Done" + RESET);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    System.out.println(WHITE_BOLD + "do you want to add a tags to it?(type you'r tag or 0 to exit)" + RESET);
                    input = scanner.nextLine();
                    while (!input.equals("0")) {
                        try {
                            PreparedStatement pStatement = insta.con.prepareStatement("CALL insert_tag(?)");
                            pStatement.setString(1, input);
                            pStatement.executeQuery();
                            pStatement.close();
                            System.out.println(GREEN_BOLD + "tag added!" + RESET);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        System.out.println(WHITE_BOLD + "do you want to add a tags to it?(type you'r tag or 0 to exit)" + RESET);
                        input = scanner.nextLine();
                    }

                    break;
                case "3":
                    try {
                        CallableStatement cStmt = insta.con.prepareCall("{? = call followers_count(?)}");
                        cStmt.registerOutParameter(1, Types.INTEGER);
                        cStmt.setString(2, insta.username);
                        cStmt.execute();
                        int outputValue = cStmt.getInt(1);
                        cStmt.close();
                        System.out.println(BLUE_BOLD + "you have " + outputValue + " followers" + RESET);
                        System.out.println(WHITE_BOLD + "do you want to see them?(y/n)" + RESET);
                        input = scanner.nextLine().toLowerCase();
                        if (input.equals("y") || input.equals("ye") || input.equals("yes")) {
                            PreparedStatement pStatement = insta.con.prepareStatement("CALL see_followers (?)");
                            pStatement.setString(1, insta.username);
                            ResultSet rSet2 = pStatement.executeQuery();
                            while (rSet2.next()) {
                                System.out.println(BLUE + rSet2.getString(1) + RESET);
                            }
                            pStatement.close();
                            System.out.println(WHITE_BOLD + "do you want to remove some one?(y/n)" + RESET);
                            input = scanner.nextLine().toLowerCase();
                            while (input.equals("y") || input.equals("ye") || input.equals("yes")) {
                                System.out.println(WHITE_BOLD + "please type his/her username:" + RESET);
                                input = scanner.nextLine();
                                pStatement = insta.con.prepareStatement("CALL x_follow_y (?,?,?)");
                                pStatement.setString(2, insta.username);
                                pStatement.setString(1, input);
                                pStatement.setBoolean(3, false);
                                pStatement.executeQuery();
                                pStatement.close();
                                System.out.println(GREEN_BOLD + "Done" + RESET);
                                System.out.println(WHITE_BOLD + "do you want to remove some one?(y/n)" + RESET);
                                input = scanner.nextLine().toLowerCase();
                            }
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    break;
                case "4":
                    try {
                        CallableStatement cStmt2 = insta.con.prepareCall("{? = call followees_count(?)}");
                        cStmt2.registerOutParameter(1, Types.INTEGER);
                        cStmt2.setString(2, insta.username);
                        cStmt2.execute();
                        int outputValue2 = cStmt2.getInt(1);
                        cStmt2.close();
                        System.out.println(BLUE_BOLD + "you are following " + outputValue2 + " people" + RESET);
                        System.out.println(WHITE_BOLD + "do you want to see them?(y/n)" + RESET);
                        input = scanner.nextLine().toLowerCase();
                        if (input.equals("y") || input.equals("ye") || input.equals("yes")) {
                            PreparedStatement pStatement = insta.con.prepareStatement("CALL see_followees (?)");
                            pStatement.setString(1, insta.username);
                            ResultSet rSet2 = pStatement.executeQuery();
                            while (rSet2.next()) {
                                System.out.println(BLUE + rSet2.getString(1) + RESET);
                            }
                            pStatement.close();
                            System.out.println(WHITE_BOLD + "do you want to unfollow some one?(y/n)" + RESET);
                            input = scanner.nextLine().toLowerCase();
                            while (input.equals("y") || input.equals("ye") || input.equals("yes")) {
                                System.out.println(WHITE_BOLD + "please type his/her username:" + RESET);
                                input = scanner.nextLine();
                                pStatement = insta.con.prepareStatement("CALL x_follow_y (?,?,?)");
                                pStatement.setString(1, insta.username);
                                pStatement.setString(2, input);
                                pStatement.setBoolean(3, false);
                                pStatement.executeQuery();
                                pStatement.close();
                                System.out.println(GREEN_BOLD + "Done" + RESET);
                                System.out.println(WHITE_BOLD + "do you want to unfollow some one?(y/n)" + RESET);
                                input = scanner.nextLine().toLowerCase();
                            }
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    break;
                case "5":

                    try {
                        System.out.println(WHITE_BOLD + "these are users who was active in this month!:" + RESET);
                        PreparedStatement pStatement = insta.con.prepareStatement("CALL active_users (?)");
                        pStatement.setInt(1, 1);
                        ResultSet rSet2 = pStatement.executeQuery();
                        while (rSet2.next()) {
                            System.out.println(BLUE + rSet2.getString(2) + RESET);
                        }
                        pStatement.close();
                        System.out.println(WHITE_BOLD + "witch one do you want to follow?(0 to exit)");
                        input = scanner.nextLine();
                        while (!input.equals("0")) {
                            pStatement = insta.con.prepareStatement("CALL x_follow_y (?,?,?)");
                            pStatement.setString(1, insta.username);
                            pStatement.setString(2, input);
                            pStatement.setBoolean(3, true);
                            pStatement.executeQuery();
                            pStatement.close();
                            System.out.println(GREEN_BOLD + "Done" + RESET);
                            System.out.println(WHITE_BOLD + "witch one do you want to follow?(0 to exit)");
                            input = scanner.nextLine();
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    break;
                case "6":
                    try {
                        System.out.println(WHITE_BOLD + "please set a username for him" + RESET);
                        input = scanner.nextLine();
                        PreparedStatement pStatement = insta.con.prepareStatement("INSERT INTO users(username) VALUES (?)");
                        pStatement.setString(1, input);
                        pStatement.execute();
                        pStatement.close();
                        pStatement = insta.con.prepareStatement("CALL add_account_pro(?)");
                        pStatement.setString(1, input);
                        pStatement.executeQuery();
                        pStatement.close();
                        System.out.println(GREEN_BOLD + "USER ADDED" + RESET);
                    } catch (SQLException throwables) {
                        System.out.println(RED_BOLD + "could not add him" + RESET);
                        throwables.printStackTrace();
                    }
                    break;
                case "7":
                    while (true) {
                        System.out.println(WHITE_BOLD + "1.application details and users" + RESET);
                        System.out.println(WHITE_BOLD + "2.insert temp data (test)" + RESET);
                        System.out.println(WHITE_BOLD + "3.clear all tables (test)" + RESET);
                        System.out.println(WHITE_BOLD + "4.change password" + RESET);
                        System.out.println(WHITE_BOLD + "0.return home" + RESET);
                        input = scanner.nextLine();
                        if (input.equals("0"))
                            break;
                        else if (input.equals("1")) {
                            try {
                                CallableStatement cStmt2 = insta.con.prepareCall("{? = call database_created_at()}");
                                cStmt2.registerOutParameter(1, Types.DATE);
                                cStmt2.execute();
                                String outputValue2 = cStmt2.getString(1);
                                cStmt2.close();
                                System.out.println(BLUE_BOLD + "instagram " + outputValue2 + RESET);
                                System.out.println(YELLOW + "year                month               day                 people" + RESET);
                                PreparedStatement pStatement = insta.con.prepareStatement("CALL user_created_at_report()");
                                ResultSet rSet = pStatement.executeQuery();
                                int temp;
                                System.out.println(BLUE);
                                while (rSet.next()) {
                                    for (int i = 1; i <= 4; i++) {
                                        System.out.print(rSet.getString(i));
                                        temp = 20 - rSet.getString(i).length();
                                        for (int j = 0; j < temp; j++) {
                                            System.out.print(" ");
                                        }
                                    }
                                    System.out.println();
                                }
                                System.out.println(RESET);
                                rSet.close();
                                pStatement.close();

                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }

                        } else if (input.equals("2")) {
                            if (!insta.username.equals("instagram_admin")) {
                                System.out.println(RED_BOLD + "you should be admin to do that!" + RESET);
                                continue;
                            }
                            try {
                                PreparedStatement pStatement = insta.con.prepareStatement("CALL insert_temp_data()");
                                pStatement.executeQuery();
                                pStatement.close();
                                System.out.println(GREEN_BOLD + "INSERT TEMP DATA COMPLETED!!" + RESET);
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        } else if (input.equals("3")) {
                            if (!insta.username.equals("instagram_admin")) {
                                System.out.println(RED_BOLD + "you should be admin to do that!" + RESET);
                                continue;
                            }
                            try {
                                PreparedStatement pStatement = insta.con.prepareStatement("CALL clear_all_tables()");
                                pStatement.executeQuery();
                                pStatement.close();
                                System.out.println(GREEN_BOLD + "DELETE ALL DATA COMPLETED!!" + RESET);
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        } else if (input.equals("4")) {
                            System.out.println(WHITE_BOLD + "please enter you'r new password" + RESET);
                            String pass1 = scanner.nextLine();
                            System.out.println(WHITE_BOLD + "please reenter you'r password" + RESET);
                            String pass2 = scanner.nextLine();
                            if (pass1.equals(pass2)) {
                                try {
                                    PreparedStatement pStatement = insta.con.prepareStatement("CALL change_password(? , ?)");
                                    pStatement.setString(1, insta.username);
                                    pStatement.setString(2, pass1);
                                    pStatement.executeQuery();
                                    pStatement.close();
                                    System.out.println(GREEN_BOLD + "password changed!" + RESET);
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }
                            } else {
                                System.out.println(RED_BOLD + "the passwords doesn't match!!\nplease try again" + RESET);
                            }
                        } else
                            System.out.println(RED_BOLD + "wrong input" + RESET);
                    }

                    break;
                case "8":
                    work = false;
                    break;
                default:
                    System.out.println(RED_BOLD + "WRONG INPUT" + RESET);
                    break;
            }
            if (!work)
                break;
        }
        try {
            insta.con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
