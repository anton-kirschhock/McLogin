package org.zapto.muziedev.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class MySqlConnection {

    private String ServerAddress;
    private String ServerPort;
    private String ServerDatabase;
    private String Username;
    private String Password;
    private boolean ConnectionIsOpen = false;
    private Connection con;
    private Statement cmd;
    private ResultSet r; 
    private String ConstrTmpl = "jdbc:mysql://{0}:{1}/{2}";
    private String Constr = "jdbc:mysql://{0}:{1}/{2}";
    private Logger log = Logger.getLogger(MySqlConnection.class.getName());

    public MySqlConnection(String serveraddress, String serverport, String serverdatabase, String username, String password) {
        ServerAddress = serveraddress;
        ServerPort = serverport;
        ServerDatabase = serverdatabase;
        Username = username;
        Password = password;
        reloadConstr();
        //log.log(Level.INFO, "init is complete!");
    }

    public void reloadConstr() {//HERE!
        Constr = ConstrTmpl;
        Constr = Constr.replace("{0}", ServerAddress);
        Constr = Constr.replace("{1}", ServerPort);
        Constr = Constr.replace("{2}", ServerDatabase);
        //System.out.println(Constr);
        //log.log(Level.INFO, "Reloading connectionstring is complete!");
    }

    /**
     * This function returns a string based array (delimited per row with "~"
     * and per column "|") If return = null, then there is an exception or no
     * parameters in the function.
     *
     * @param sql The SQL-string
     * @param columnlength The length of the columns (=the amount of columns in
     * the SELECT statement) (use static void getColumnlengthFromSql if
     * required)
     * @return String based array s(delimited per row with "~" and per column
     * "|")
     *
     */
    public List<String> getData(String sql, int col) {
        List<String> returnee = new ArrayList();
        if (sql == "" || col == 0) {
            log.log(Level.SEVERE, "A parameter is missing!");
            returnee = null;
        } else {
            con = null;
            cmd = null;
            r = null;
            try {
                con = DriverManager.getConnection(Constr, Username, Password);
                con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                cmd = con.createStatement();
                r = cmd.executeQuery(sql);

                while (!r.isLast()) {
                    returnee.add(r.getString(col));
                    r.next(); // moves to next cursor
                }
            } catch (Exception ex) {
                printError("An exception occured: " + ex.toString() + "; With sql=" + sql);
                returnee = null;
            } finally {
                try {
                    if (cmd != null) {
                        cmd.close();
                    }
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException ex) {
                    //ignore this one..
                }
            }
        }
        return returnee;
    }

    public static int getColumnlengthFromSql(String sql) {
        return sql.split("\\FROM")[0].split("\\,").length;
    }

    private void printError(String msg) {
        log.log(Level.SEVERE, msg);
    }

    public boolean execNonQuery(String sql) {
        boolean returnee = false;
        if (sql == "" || sql == null) {
            printError("No SQL At function 'execNonQuery' where SQL=Required!");
            returnee = false;
        } else {

            con = null;
            cmd = null;
            try {
                con = DriverManager.getConnection(Constr, Username, Password);
                cmd = con.createStatement();
                cmd.executeUpdate(sql);
            } catch (SQLException ex) {
                printError("An exception occured: " + ex.toString() + "; With sql=" + sql);
                returnee = false;
            } finally {
                try {
                    if (cmd != null) {
                        cmd.close();
                    }
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException ex) {
                    //ignore this one..
                }
            }
        }
        return returnee;
    }

        public String getFirst2Result(String sql) {
        String returnee = "";
        if (sql == "" || sql == null) {
            printError("No SQL At function 'getFirstResult' where SQL=Required!");
            returnee = null;
        } else {
            con = null;
            cmd = null;
            r = null;
            try {
                con = DriverManager.getConnection(Constr, Username, Password);
                cmd = con.createStatement();
                r = cmd.executeQuery(sql);
                if (r.first() && r.getString(1) != null) {
                        returnee = r.getString(1) + "|" + r.getString(2);
                }
            } catch (SQLException ex) {
                printError("An exception occured: " + ex.toString() + "; With sql=" + sql);
                returnee = null;
            } finally {
                try {
                    if (r != null) {
                        r.close();
                    }
                    if (cmd != null) {
                        cmd.close();
                    }
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException ex) {
                    //ignore this one..
                }
            }
        }
        return returnee;
    }
    
    public String getFirstResult(String sql, int column) {
        String returnee = "";
        if (sql == "" || sql == null) {
            printError("No SQL At function 'getFirstResult' where SQL=Required!");
            returnee = null;
        } else {
            con = null;
            cmd = null;
            r = null;
            try {
                con = DriverManager.getConnection(Constr, Username, Password);
                con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                cmd = con.createStatement();
                r = cmd.executeQuery(sql);
                if (r.first()) {
                    if (r.getString(column) != null) {
                        returnee = r.getString(column);
                    }
                }
            } catch (SQLException ex) {
                printError("An exception occured: " + ex.toString() + "; With sql=" + sql);
                returnee = null;
            } finally {
                try {
                    if (r != null) {
                        r.close();
                    }
                    if (cmd != null) {
                        cmd.close();
                    }
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException ex) {
                    //ignore this one..
                }
            }
        }
        return returnee;
    }

    public boolean getQueryExists(String sql) {
        boolean returnee = false;
        if (sql == "" || sql == null) {
            printError("No SQL At function 'getQueryExists' where SQL=Required!");
        } else {
            con = null;
            cmd = null;
            r = null;
            try {
                con = DriverManager.getConnection(Constr, Username, Password);
                cmd = con.createStatement();
                r = cmd.executeQuery(sql);
                int i = 0;
                while (r.next()) {
                    i += 1;
                }
                if (i > 0) {
                    returnee = true;
                } else {
                    returnee = false;
                }
            } catch (SQLException ex) {
                printError("An exception occured: " + ex.toString() + "; With sql=" + sql);
                returnee = false;
            } finally {
                try {
                    if (r != null) {
                        r.close();
                    }
                    if (cmd != null) {
                        cmd.close();
                    }
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException ex) {
                    //ignore this one..
                }
            }
        }
        return returnee;
    }

    private String getServerAddress() {
        return ServerAddress;
    }

    private void setServerAddress(String ServerAddress) {
        this.ServerAddress = ServerAddress;
    }

    private String getServerPort() {
        return ServerPort;
    }

    private void setServerPort(String ServerPort) {
        this.ServerPort = ServerPort;
    }

    private String getServerDatabase() {
        return ServerDatabase;
    }

    private void setServerDatabase(String ServerDatabase) {
        this.ServerDatabase = ServerDatabase;
    }

    private String getUsername() {
        return Username;
    }

    private void setUsername(String Username) {
        this.Username = Username;
    }

    private String getPassword() {
        return Password;
    }

    private void setPassword(String Password) {
        this.Password = Password;
    }

    public boolean isConnectionIsOpen() {
        return ConnectionIsOpen;
    }

    private void setConnectionIsOpen(boolean ConnectionIsOpen) {
        this.ConnectionIsOpen = ConnectionIsOpen;
    }

    private Connection getCon() {
        return con;
    }

    private void setCon(Connection con) {
        this.con = con;
    }

    private Statement getCmd() {
        return cmd;
    }

    private void setCmd(Statement cmd) {
        this.cmd = cmd;
    }

    public ResultSet getR() {
        return r;
    }

    private void setR(ResultSet r) {
        this.r = r;
    }

    private String getConstrTmpl() {
        return ConstrTmpl;
    }

    private void setConstrTmpl(String ConstrTmpl) {
        this.ConstrTmpl = ConstrTmpl;
    }

    private String getConstr() {
        return Constr;
    }

    private void setConstr(String Constr) {
        this.Constr = Constr;
    }

    private Logger getLog() {
        return log;
    }

    private void setLog(Logger log) {
        this.log = log;
    }
}
