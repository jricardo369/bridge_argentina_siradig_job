/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import java.io.Serializable;
import vo.AppParamVO;

public class ParamHelperVO
  extends AppParamVO
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private static final long releaseVersionID = 0L;
  private Integer nivel;
  private String nivelName;
  
  public ParamHelperVO()
  {
    setNivel(null);
    setNivelName("gry");
  }
  
  public Integer getNivel()
  {
    return this.nivel;
  }
  
  public String getNivelName()
  {
    return this.nivelName;
  }
  
  public void setNivelName(String nivelName)
  {
    this.nivelName = nivelName;
  }
  
  public void setNivel(Integer nivel)
  {
    this.nivel = nivel;
  }
  
  public String getVOInfo()
  {
    return "Clase ParamHelperVO. Versi�n 1.0";
  }
}
