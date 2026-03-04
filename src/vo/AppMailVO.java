/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vo;

import java.io.Serializable;

public class AppMailVO
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private static final long releaseVersionID = 0L;
  private String id;
  private String name;
  private String server;
  private String from;
  private String subject;
  private String frames;
  private boolean logged;
  private int logExpired;
  private String active;
  private String adminMail;
  private String instance;
  
  public String getId()
  {
    return this.id;
  }
  
  public String getActive()
  {
    return this.active;
  }
  
  public String getAdminMail()
  {
    return this.adminMail;
  }
  
  public String getFrames()
  {
    return this.frames;
  }
  
  public String getFrom()
  {
    return this.from;
  }
  
  public String getInstance()
  {
    return this.instance;
  }
  
  public int getLogExpired()
  {
    return this.logExpired;
  }
  
  public boolean isLog()
  {
    return this.logged;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String getServer()
  {
    return this.server;
  }
  
  public String getSubject()
  {
    return this.subject;
  }
  
  public void setSubject(String subject)
  {
    this.subject = subject;
  }
  
  public void setServer(String server)
  {
    this.server = server;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public void setLogged(String log, int logexp)
  {
    this.logged = (!log.equals("N"));
    if (this.logged) {
      this.logExpired = logexp;
    } else {
      this.logExpired = 0;
    }
  }
  
  public void setInstance(String instance)
  {
    this.instance = instance;
  }
  
  public void setFrom(String from)
  {
    this.from = from;
  }
  
  public void setFrames(String frames)
  {
    this.frames = frames;
  }
  
  public void setAdminMail(String adminMail)
  {
    this.adminMail = adminMail;
  }
  
  public void setActive(String active)
  {
    this.active = active;
  }
  
  public void setId(String id)
  {
    this.id = id;
  }
  
  public String getVOInfo()
  {
    return "Clase AppMailVO. Versi�n 1.0";
  }
}
