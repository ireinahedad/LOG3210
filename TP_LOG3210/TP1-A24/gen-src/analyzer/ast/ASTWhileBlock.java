/* Generated By:JJTree: Do not edit this line. ASTWhileBlock.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package analyzer.ast;

public
class ASTWhileBlock extends SimpleNode {
  public ASTWhileBlock(int id) {
    super(id);
  }

  public ASTWhileBlock(Parser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ParserVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=9a6a902191e3509e583458bd8f6007b2 (do not edit this line) */
