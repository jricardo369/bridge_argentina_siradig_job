/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vo;

import java.io.Serializable;

public class AppParamVO
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private static final long releaseVersionID = 0L;
  private String id;
  private String name;
  private String title;
  private String cmpType;
  private String min;
  private String max;
  private String type;
  private String format;
  private String unit;
  private String image;
  private String report;
  private String exc;
  private String value;
  
  public String getId()
  {
    return this.id;
  }
  
  public String getCmpType()
  {
    return this.cmpType;
  }
  
  public String getExc()
  {
    return this.exc;
  }
  
  public String getFormat()
  {
    return this.format;
  }
  
  public String getImage()
  {
    return this.image;
  }
  
  public String getMax()
  {
    return this.max;
  }
  
  public String getMin()
  {
    return this.min;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String getReport()
  {
    return this.report;
  }
  
  public String getTitle()
  {
    return this.title;
  }
  
  public String getType()
  {
    return this.type;
  }
  
  public String getUnit()
  {
    return this.unit;
  }
  
  public String getValue()
  {
    return this.value;
  }
  
  public void setValue(String value)
  {
    this.value = value;
  }
  
  public void setUnit(String unit)
  {
    this.unit = unit;
  }
  
  public void setType(String type)
  {
    this.type = type;
  }
  
  public void setTitle(String title)
  {
    this.title = title;
  }
  
  public void setReport(String report)
  {
    this.report = report;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public void setMin(String min)
  {
    this.min = min;
  }
  
  public void setMax(String max)
  {
    this.max = max;
  }
  
  public void setImage(String image)
  {
    this.image = image;
  }
  
  public void setFormat(String format)
  {
    this.format = format;
  }
  
  public void setExc(String exc)
  {
    this.exc = exc;
  }
  
  public void setCmpType(String cmpType)
  {
    this.cmpType = cmpType;
  }
  
  public void setId(String id)
  {
    this.id = id;
  }
  
  public String getVOInfo()
  {
    return "Clase AppParamVO. Versi�n 1.0";
  }
}
