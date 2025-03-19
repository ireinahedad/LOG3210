package analyzer.visitors;

import analyzer.ast.*;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;


/**
 * Ce visiteur explore l'AST et génère du code intermédiaire.
 *
 * @author Félix Brunet
 * @author Doriane Olewicki
 * @author Quentin Guidée
 * @author Raphaël Tremblay
 * @version 2024.02.26
 */
public class IntermediateCodeGenVisitor implements ParserVisitor {
    private final PrintWriter m_writer;

    public HashMap<String, VarType> SymbolTable = new HashMap<>();
    public HashMap<String, Integer> EnumValueTable = new HashMap<>();

    private int id = 0;
    private int label = 0;

    public IntermediateCodeGenVisitor(PrintWriter writer) {
        m_writer = writer;
    }

    private String newID() {
        return "_t" + id++;
    }

    private String newLabel() {
        return "_L" + label++;
    }

    @Override
    public Object visit(SimpleNode node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTProgram node, Object data) {
        // node.childrenAccept(this, data);
        String endLabel = newLabel();
        node.childrenAccept(this, endLabel);
        m_writer.println(endLabel);
        return null;
        // TODO

    }

    @Override
    public Object visit(ASTDeclaration node, Object data) {
        String varName = ((ASTIdentifier) node.jjtGetChild(0)).getValue();
        VarType varType;

        if (node.getValue() == null) {
            varName = ((ASTIdentifier) node.jjtGetChild(1)).getValue();
            varType = VarType.EnumVar;
        } else
            varType = node.getValue().equals("num") ? VarType.Number : VarType.Bool;

        SymbolTable.put(varName, varType);

        return null;
    }

    @Override
    public Object visit(ASTBlock node, Object data) {

        if (node.jjtGetNumChildren() == 1) {
            node.jjtGetChild(0).jjtAccept(this, data);
        } else {
            for (int i = 0; i < node.jjtGetNumChildren(); i++) {
                if (i != node.jjtGetNumChildren() - 1) {
                    String lb = newLabel();
                    node.jjtGetChild(i).jjtAccept(this, lb);
                    m_writer.println(lb);
                } else {
                    node.jjtGetChild(i).jjtAccept(this, data);
                }
            }
        }

        return null;

        // TODO
    }

    @Override
    public Object visit(ASTEnumStmt node, Object data) {
        String enumName = ((ASTIdentifier) node.jjtGetChild(0)).getValue();
        int enumValue = 0;
        for (int i = 1; i < node.jjtGetNumChildren(); i++) {
            String valueName = ((ASTIdentifier) node.jjtGetChild(i)).getValue();
            EnumValueTable.put(valueName, enumValue++);
        }
        return null;
        //TODO
    }

    @Override
    public Object visit(ASTSwitchStmt node, Object data) {
        String begin = newLabel();
        m_writer.println("goto " + begin);

        String switchVar = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String[] labels = new String[node.jjtGetNumChildren()];
        String[] caseVars = new String[node.jjtGetNumChildren()];

        for (int i = 1; i < node.jjtGetNumChildren(); i++) {
            String newLabel = newLabel();
            labels[i] = newLabel;
            m_writer.println(newLabel);
            caseVars[i] = (String) node.jjtGetChild(i).jjtAccept(this, data);
            m_writer.println("goto " + data);
        }

        m_writer.println(begin);
        for (int i = 1; i < node.jjtGetNumChildren() - 1; i++) {
            m_writer.println("if " + switchVar + " == " + caseVars[i] + " goto " + labels[i]);
        }
        if (node.jjtGetNumChildren() == 2) {
            m_writer.println("if " + switchVar + " == " + caseVars[1] + " goto " + labels[1]);
        } else {
            m_writer.println("goto " + labels[node.jjtGetNumChildren() - 1]);
        }

        return null;
        // TODO

    }

    @Override
    public Object visit(ASTCaseStmt node, Object data) {
        /*
        String caseLabel = newLabel();
        m_writer.println(caseLabel);
        node.childrenAccept(this, data);
        return null;
        */
        String value = (String) node.jjtGetChild(0).jjtAccept(this, data);
        node.jjtGetChild(1).jjtAccept(this, data);
        return value;
        // TODO
    }

    @Override
    public Object visit(ASTBreakStmt node, Object data) {
        node.childrenAccept(this, data);
        m_writer.println("goto " + data);
        System.out.println("BReakstmt" + data);
        // TODO
        return null;
    }

    @Override
    public Object visit(ASTStmt node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }

    @Override
    public Object visit(ASTIfStmt node, Object data) {
        //node.childrenAccept(this, data);
        // TODO
        if (node.jjtGetNumChildren() == 2) {
            BoolLabel bl = new BoolLabel(newLabel(), (String) data);
            node.jjtGetChild(0).jjtAccept(this, bl);
            m_writer.println(bl.lTrue);
            node.jjtGetChild(1).jjtAccept(this, data);
        } else if (node.jjtGetNumChildren() == 3) {
            BoolLabel bl = new BoolLabel(newLabel(), newLabel());
            node.jjtGetChild(0).jjtAccept(this, bl);
            m_writer.println(bl.lTrue);
            node.jjtGetChild(1).jjtAccept(this, data);
            m_writer.println("goto " + data);
            m_writer.println(bl.lFalse);
            node.jjtGetChild(2).jjtAccept(this, data);
        } else {
            System.out.println("problem");
        }
        return null;

    }

    @Override
    public Object visit(ASTWhileStmt node, Object data) {

        // TODO
        String begin = newLabel();
        BoolLabel bl = new BoolLabel(newLabel(), (String) data);
        m_writer.println(begin);
        node.jjtGetChild(0).jjtAccept(this, bl);
        m_writer.println(bl.lTrue);
        node.jjtGetChild(1).jjtAccept(this, begin);
        m_writer.println("goto " + begin);
        return null;

    }

    @Override
    public Object visit(ASTForStmt node, Object data) {
        node.childrenAccept(this, data);
        // TODO
        return null;
    }

    @Override
    public Object visit(ASTAssignStmt node, Object data) {
        ASTIdentifier varNode = (ASTIdentifier) node.jjtGetChild(0);
        String varName = varNode.getValue();

        if (SymbolTable.get(varName) == VarType.Number || SymbolTable.get(varName) == VarType.EnumVar) {
            String addr = (String) node.jjtGetChild(1).jjtAccept(this, data);


            System.out.println("variabe est" + varName);
            System.out.println("ladresse est" + addr);

            m_writer.println(varName + " = " + addr);

        } else {
            BoolLabel bl = new BoolLabel(newLabel(), newLabel());
            node.jjtGetChild(1).jjtAccept(this, bl);


            m_writer.println(bl.lTrue);
            m_writer.println(varName + " = 1");
            m_writer.println("goto " + data);
            m_writer.println(bl.lFalse);
            m_writer.println(varName + " = 0");
        }

        return varName;
        // TODO
    }

    @Override
    public Object visit(ASTExpr node, Object data) {
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    public Object codeExtAddMul(SimpleNode node, Object data, Vector<String> ops) {

        if (node.jjtGetNumChildren() == 1) {
            return node.jjtGetChild(0).jjtAccept(this, data);
        }

        String op = " " + ops.firstElement() + " ";  // Assumes the operator is the same for all child operations
        String addr = newID();  // Temporary variable to store the result
        String tmp = "";  // String to build the intermediate code


        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            if (i % 2 == 0) {  // For even indices, generate a temporary variable
                String res = (String) node.jjtGetChild(i).jjtAccept(this, data);
                tmp += addr + " = " + res;
            } else {  // For odd indices, apply the operator and output the result
                tmp += op + node.jjtGetChild(i).jjtAccept(this, data);
                m_writer.println(tmp);  // Output the intermediate code
                tmp = "";  // Reset the temporary string
            }
        }
        return addr;
        // TODO
    }

    @Override
    public Object visit(ASTAddExpr node, Object data) {
        return codeExtAddMul(node, data, node.getOps());
    }

    @Override
    public Object visit(ASTMulExpr node, Object data) {
        return codeExtAddMul(node, data, node.getOps());
    }

    @Override
    public Object visit(ASTUnaExpr node, Object data) {

        // TODO
        Vector<String> operators = node.getOps();
        if (operators.isEmpty()) {
            return node.jjtGetChild(0).jjtAccept(this, data);
        }

        String addr = "";

        for (int i = 0; i < operators.size(); i++) {
            String tmp = "";
            String op = operators.get(i);
            if (i == 0) {
                String res = (String) node.jjtGetChild(0).jjtAccept(this, data);
                tmp = newID();
                m_writer.println(tmp + " = " + op + " " + res);
            } else {
                tmp = newID();
                m_writer.println(tmp + " = " + op + " " + addr);
            }
            addr = tmp;
        }

        return addr;
    }

    @Override
    public Object visit(ASTBoolExpr node, Object data) {
        if (node.jjtGetNumChildren() == 1) {
            return node.jjtGetChild(0).jjtAccept(this, data);
        }

        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            if (i % 2 == 0) {
                String op = (i == 0) ? (String) node.getOps().get(0) : (String) node.getOps().get(i - 1);
                if (op.equals("&&")) {
                    BoolLabel bl = new BoolLabel(newLabel(), ((BoolLabel) data).lFalse);
                    node.jjtGetChild(i).jjtAccept(this, bl);
                    m_writer.println(bl.lTrue);
                } else if (op.equals("||")) {
                    BoolLabel bl = new BoolLabel(((BoolLabel) data).lTrue, newLabel());
                    node.jjtGetChild(i).jjtAccept(this, bl);
                    m_writer.println(bl.lFalse);
                }
            } else {
                node.jjtGetChild(i).jjtAccept(this, data);
            }
        }

        return null;
        // TODO

    }

    @Override
    public Object visit(ASTCompExpr node, Object data) {

        // TODO
        if (node.jjtGetNumChildren() == 1) {
            return node.jjtGetChild(0).jjtAccept(this, data);
        }

        m_writer.println("if " + node.jjtGetChild(0).jjtAccept(this, data) + " " + node.getValue() + " "
                + node.jjtGetChild(1).jjtAccept(this, data) + " goto " + ((BoolLabel) data).lTrue);
        m_writer.println("goto " + ((BoolLabel) data).lFalse);
        return null;
    }

    @Override
    public Object visit(ASTNotExpr node, Object data) {

        // TODO
        if (node.getOps().size() % 2 == 0) {
            return node.jjtGetChild(0).jjtAccept(this, data);
        } else {
            BoolLabel bl = new BoolLabel(((BoolLabel) data).lFalse, ((BoolLabel) data).lTrue);
            return node.jjtGetChild(0).jjtAccept(this, bl);
        }
    }

    @Override
    public Object visit(ASTGenValue node, Object data) {

        return node.jjtGetChild(0).jjtAccept(this, data);

        // TODO
    }


    @Override
    public Object visit(ASTBoolValue node, Object data) {
        //TODO
        if (node.getValue()) {
            m_writer.println("goto " + ((BoolLabel) data).lTrue);
        } else {
            m_writer.println("goto " + ((BoolLabel) data).lFalse);
        }
        return null;
    }

    @Override
    public Object visit(ASTIdentifier node, Object data) {
        // TODO
        if (SymbolTable.get(node.getValue()) == VarType.Bool) {
            m_writer.println("if " + node.getValue() + " == 1 goto " + ((BoolLabel) data).lTrue);
            m_writer.println("goto " + ((BoolLabel) data).lFalse);
        }
        return node.getValue();
    }

    @Override
    public Object visit(ASTIntValue node, Object data) {
        //TODO
        return Integer.toString(node.getValue());

    }


    public enum VarType {
        Bool,
        Number,
        EnumType,
        EnumVar,
        EnumValue
    }

    private static class BoolLabel {
        public String lTrue;
        public String lFalse;

        public BoolLabel(String lTrue, String lFalse) {
            this.lTrue = lTrue;
            this.lFalse = lFalse;
        }
    }
}
