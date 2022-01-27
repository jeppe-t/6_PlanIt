package now.planit.Data.Repo;

import now.planit.Data.Utility.DBManager;
import now.planit.Exceptions.DBConnFailedException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MapperDB {
  Connection connection;
  PreparedStatement ps;
  ResultSet rs;
  int validate = 0;

  //Check connection with DBManager
  public PreparedStatement checkConnection(String sqlCommand)  {
    try {
      connection = DBManager.getConnection();
      ps = connection.prepareStatement(sqlCommand);
    } catch (SQLException e) {
      System.out.println("Database unavailable for checkConnection method");
      e.printStackTrace();
      throw new DBConnFailedException("Please contact support on the \"Mail us\" on the bottom of this page ");
    }
    return ps;
  }

  //Loads Parameters into Prepared statement
  public PreparedStatement setParameters(ArrayList<String> parameters) {
    try {
      for (int i = 0; i < parameters.size(); i++) {
        ps.setString(i + 1, parameters.get(i));
      }
    } catch (SQLException e) {
      System.out.println("Database unavailable for setParameters method");
      e.printStackTrace();
    }
    return ps;
  }

  //Insert or delete.
  public void insert(String sqlCommand, ArrayList<String> parameters) {
    try {
      ps = checkConnection(sqlCommand);
      setParameters(parameters).execute();
    } catch (SQLException e) {
      System.out.println("Database unavailable for save method!");
      e.printStackTrace();
    }
  }
  

  //Used to receive something from the database, will always return ResultSet.
  public ResultSet select(String sqlCommand, ArrayList<String> parameters) {
    try {
      ps = checkConnection(sqlCommand);
      rs = setParameters(parameters).executeQuery();
    }
    catch (SQLException e) {
      System.out.println("Database unavailable for load method!");
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
    return rs;
  }

  //Insert with update/feedback from server 0 or 1.
  public int insertUpdate(String sqlCommand, ArrayList<String> parameters) {
    try {
      ps = checkConnection(sqlCommand);
      validate = setParameters(parameters).executeUpdate();
    }
    catch (SQLException e) {
      System.out.println("Database unavailable for load method!");
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
    return validate;
  }
}