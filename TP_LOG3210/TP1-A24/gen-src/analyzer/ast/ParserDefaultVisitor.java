/* Generated By:JavaCC: Do not edit this line. ParserDefaultVisitor.java Version 7.0.2 */
package analyzer.ast;

public class ParserDefaultVisitor implements ParserVisitor{
  public Object defaultVisit(SimpleNode node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(SimpleNode node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTProgram node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTAssignStmt node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTIdentifier node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTIntValue node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTRealValue node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTWhileStmt node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTWhileCond node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTDoWhileStmt node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTIfStmt node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTForStmt node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTParentheses node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTNot node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTMinus node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTMulti node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTDivision node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTAddition node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTSoustraction node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTCompare node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTLogic node, Object data){
    return defaultVisit(node, data);
  }
}
/* JavaCC - OriginalChecksum=401be5bac1cd5be71cdade7404b5573d (do not edit this line) */
