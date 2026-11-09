package testesql;

import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class NewClass extends JFrame {

    private Connection con;
    private ResultSet rs; // recordset preenchido pelo SELECT

    // Painel 1: pesquisa
    private JLabel lblPesquisa = new JLabel("Nome:");
    private JTextField txtPesquisa = new JTextField();
    private JButton btnPesquisar = new JButton("Pesquisar");

    // Painel 2: dados
    private JLabel lblNome = new JLabel("Nome:");
    private JLabel lblSalario = new JLabel("Salário:");
    private JLabel lblCargo = new JLabel("Cargo:");
    private JTextField txtNome = new JTextField();
    private JTextField txtSalario = new JTextField();
    private JTextField txtCargo = new JTextField();

    // Painel 3: navegação
    private JButton btnAnterior = new JButton("Anterior");
    private JButton btnProximo = new JButton("Próximo");
    private JLabel lblPosicao = new JLabel(" ", SwingConstants.CENTER);

    public NewClass() {
        super("TRABALHO PRATICO 04");
        setLayout(new BorderLayout());

        // ---------- Painel 1: pesquisa ----------
        JPanel pnlPesquisa = new JPanel(new GridLayout(2, 1, 0, 5));
        pnlPesquisa.setPreferredSize(new Dimension(430, 80));

        JPanel linhaNome = new JPanel(new BorderLayout(10, 0));
        lblPesquisa.setHorizontalAlignment(SwingConstants.LEFT);
        linhaNome.add(lblPesquisa, BorderLayout.WEST);
        linhaNome.add(txtPesquisa, BorderLayout.CENTER);
        linhaNome.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        txtPesquisa.setHorizontalAlignment(SwingConstants.LEFT);

        JPanel linhaBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        linhaBotao.add(btnPesquisar);

        pnlPesquisa.add(linhaNome);
        pnlPesquisa.add(linhaBotao);

        // ---------- Painel 2: dados ----------
        JPanel pnlDados = new JPanel(new GridLayout(3, 2, 5, 5));
        pnlDados.add(lblNome);
        pnlDados.add(txtNome);
        pnlDados.add(lblSalario);
        pnlDados.add(txtSalario);
        pnlDados.add(lblCargo);
        pnlDados.add(txtCargo);

        txtNome.setEditable(false);
        txtSalario.setEditable(false);
        txtCargo.setEditable(false);

        // ---------- Painel 3: navegação (rodapé) ----------
        JPanel pnlNavegacao = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnAnterior.setPreferredSize(new Dimension(100, 30));
        btnProximo.setPreferredSize(new Dimension(100, 30));
        lblPosicao.setPreferredSize(new Dimension(70, 30));
        pnlNavegacao.add(btnAnterior);
        pnlNavegacao.add(lblPosicao);
        pnlNavegacao.add(btnProximo);

        btnAnterior.setEnabled(false);
        btnProximo.setEnabled(false);

        add(pnlPesquisa, BorderLayout.NORTH);
        add(pnlDados, BorderLayout.CENTER);
        add(pnlNavegacao, BorderLayout.SOUTH);

        // ---------- Eventos ----------
        btnPesquisar.addActionListener(e -> pesquisar());
        txtPesquisa.addActionListener(e -> pesquisar()); // Enter também pesquisa

        btnProximo.addActionListener(e -> {
            try {
                if (rs != null && rs.next()) {
                    mostrarRegistro();
                }
                atualizarNavegacao();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        });

        btnAnterior.addActionListener(e -> {
            try {
                if (rs != null && rs.previous()) {
                    mostrarRegistro();
                }
                atualizarNavegacao();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                txtPesquisa.requestFocusInWindow();
            }

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                try { if (con != null) con.close(); } catch (SQLException ex) { }
            }
        });

        setSize(430, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    // SELECT com LIKE usando PreparedStatement; preenche o recordset (rs)
    private void pesquisar() {
        String nome = txtPesquisa.getText().trim();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um nome para pesquisar.");
            return;
        }

        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }

            con = Conexao.getConexao();

            String sql = "SELECT f.nome_func, f.sal_func, c.ds_cargo "
                       + "FROM tbfuncs f "
                       + "LEFT JOIN tbcargos c ON f.cod_cargo = c.cd_cargo "
                       + "WHERE f.nome_func LIKE ? "
                       + "ORDER BY f.nome_func";

            PreparedStatement ps = con.prepareStatement(
                    sql,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, "%" + nome + "%");

            rs = ps.executeQuery();

            if (rs.next()) {
                mostrarRegistro();
            } else {
                limparCampos();
                rs = null;
                JOptionPane.showMessageDialog(this, "Nenhum funcionário encontrado.");
            }
            atualizarNavegacao();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }

    private void mostrarRegistro() throws SQLException {
        txtNome.setText(rs.getString("nome_func").trim());
        txtSalario.setText(String.format("%.2f", rs.getDouble("sal_func")));
        String cargo = rs.getString("ds_cargo");
        txtCargo.setText(cargo == null ? "" : cargo.trim());
    }

    private void limparCampos() {
        txtNome.setText("");
        txtSalario.setText("");
        txtCargo.setText("");
    }

    // Habilita/desabilita Anterior e Próximo conforme a posição no recordset
    private void atualizarNavegacao() {
        try {
            if (rs == null || rs.isClosed()) {
                btnAnterior.setEnabled(false);
                btnProximo.setEnabled(false);
                lblPosicao.setText(" ");
                return;
            }

            int atual = rs.getRow();
            rs.last();
            int total = rs.getRow();
            rs.absolute(atual); // volta ao registro em que estava

            btnAnterior.setEnabled(atual > 1);
            btnProximo.setEnabled(atual < total);
            lblPosicao.setText(atual + " de " + total);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NewClass().setVisible(true));
    }
}