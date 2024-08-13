package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program;

import de.nikschadowsky.baall.compiler.syntax.error.SyntaxDiagnostic;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * File created on 14.04.2024
 */
public class ProgramNodeImpl extends AbstractNode implements ProgramNode {

    private ImportsNode imports;

    private StatementsNode statements;

    private ExportsNode exports;

    private List<SyntaxDiagnostic> diagnostics;

    public ProgramNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public ImportsNode getImports() {
        return imports;
    }

    public void setImports(ImportsNode imports) {
        this.imports = imports;
    }

    @Override
    public StatementsNode getStatements() {
        return statements;
    }

    public void setStatements(StatementsNode statements) {
        this.statements = statements;
    }

    @Override
    public ExportsNode getExports() {
        return exports;
    }

    public void setExports(ExportsNode exports) {
        this.exports = exports;
    }

    @Override
    public List<SyntaxDiagnostic> getSyntaxDiagnostics() {
        return diagnostics;
    }

    public void setDiagnostics(List<SyntaxDiagnostic> diagnostics) {
        this.diagnostics = new ArrayList<>(diagnostics);
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.PROGRAM;
    }
}
