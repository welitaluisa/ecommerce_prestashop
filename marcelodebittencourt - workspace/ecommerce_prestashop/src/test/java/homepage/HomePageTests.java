package homepage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import base.BaseTests;
import pages.CarrinhoPage;
import pages.CheckoutPage;
import pages.LoginPage;
import pages.ModalProdutoPage;
import pages.PedidoPage;
import pages.ProdutoPage;
import util.Funcoes;

public class HomePageTests extends BaseTests {

	@Test
	public void testContarProdutos_oitoProdutosDiferentes() {
		carregarPaginaInicial();
		assertThat(homePage.contarProdutos(), is(8));
	}

	@Test
	public void testValidarCarrinhoZerado_ZeroItensNoCarrinho() {
		int ProdutosNoCarrinho = homePage.obterQuantidadeProdutosNoCarrinho();
		// System.out.println(ProdutosNoCarrinho);
		assertThat(ProdutosNoCarrinho, is(0));

	}

	ProdutoPage produtoPage;
	String nomeProdutoPage;

	@Test
	public void testValidarDetalhesDoProduto_DescricaoEValorIguais() {
		int indice = 0;
		String nomeProdutoHomePage = homePage.obterNomeProduto(indice);
		String precoProdutoHomePage = homePage.obterPrecoProduto(indice);

		System.out.println(nomeProdutoHomePage);
		System.out.println(precoProdutoHomePage);

		produtoPage = homePage.clicarProduto(indice);

		nomeProdutoPage = produtoPage.obterNomeProduto();
		String precoProdutoPage = produtoPage.obterPrecoProduto();

		System.out.println(nomeProdutoPage);
		System.out.println(precoProdutoPage);

		assertThat(nomeProdutoHomePage.toUpperCase(), is(nomeProdutoPage.toUpperCase()));
		assertThat(precoProdutoHomePage, is(precoProdutoPage));
	}

	LoginPage loginPage;

	@Test
	public void testLoginComSucesso_UsuarioLogado() {
		// clicar no botão Sign In na home page

		loginPage = homePage.clicarBotaoSignIn();

		// Preencher usuario e senha
		loginPage.preencherEmail("marcelo@teste.com");
		loginPage.preencherPassord("marcelo");

		// clicar no bot�o Sign para logar
		loginPage.clicarBotaoSingIn();

		// Validar se o usuario est� logado de fato
		assertThat(homePage.estaLogado("Marcelo Bittencourt"), is(true));
		carregarPaginaInicial();

	}

	@ParameterizedTest
	@CsvFileSource(resources = "/massaTeste_login.csv", numLinesToSkip = 1, delimiter = ';')
	public void testLogin_UsuarioLogadoComDadosValidos(String nomeTeste, String email, String password,
			String nomeUsuario, String resultado) {
		loginPage = homePage.clicarBotaoSignIn();

		// Preencher usuario e senha
		loginPage.preencherEmail(email);
		loginPage.preencherPassord(password);

		// clicar no bot�o Sign para logar
		loginPage.clicarBotaoSingIn();

		boolean esperado_loginOk;
		if (resultado.equals("positivo"))
			esperado_loginOk = true;
		else
			esperado_loginOk = false;

		// Validar se o usuario est� logado de fato
		assertThat(homePage.estaLogado(nomeUsuario), is(esperado_loginOk));
		
		capturarTela(nomeTeste, resultado);
		
		if (esperado_loginOk)
			homePage.clicarBotaoSignOut();

		carregarPaginaInicial();
	}

	ModalProdutoPage modalProdutoPage;

	@Test
	public void testIncluirProdutoNoCarrinho_ProdutoIncluidoComSucesso() {

		String tamanhoProduto = "M";
		String corProduto = "Black";
		int quantidadeProduto = 2;

		// --Pr�-Condi��o
		// Usuario Logado

		if (!homePage.estaLogado("Marcelo Bittencourt")) {
			testLoginComSucesso_UsuarioLogado();
		}

		// --Teste
		// Selecionando Produto
		testValidarDetalhesDoProduto_DescricaoEValorIguais();

		// selecionar Tamanho
		List<String> listaOpcoes = produtoPage.obterOpcoesSelecionadas();

		System.out.println(listaOpcoes.get(0));
		System.out.println("Tamanho da lista: " + listaOpcoes.size());

		produtoPage.selecionarOpcaoDropDown(tamanhoProduto);

		listaOpcoes = produtoPage.obterOpcoesSelecionadas();

		System.out.println(listaOpcoes.get(0));
		System.out.println("Tamanho da lista: " + listaOpcoes.size());

		// Selecionar Cor

		produtoPage.selecionarCorPreta();

		// Selecionar Quantidade

		produtoPage.alterarQuantidade(quantidadeProduto);

		// Adicionar no Carrinho

		modalProdutoPage = produtoPage.clicarBotaoAddToCart();
		// assertThat(modalProdutoPage.obterMensagemProdutoAdicionado(), is("Product
		// successfully added to your shopping cart"));

		// Valida��es
		assertTrue(modalProdutoPage.obterMensagemProdutoAdicionado()
				.endsWith("Product successfully added to your shopping cart"));

		System.out.println();
		assertThat(modalProdutoPage.obterDescricaoProduto().toUpperCase(), is(nomeProdutoPage.toUpperCase()));

		String precoProdutoString = modalProdutoPage.obterPrecoProduto();
		precoProdutoString = precoProdutoString.replace("$", "");
		Double precoProduto = Double.parseDouble(precoProdutoString);

		assertThat(modalProdutoPage.obterTamanhoProduto(), is(tamanhoProduto));
		assertThat(modalProdutoPage.obterCorProduto(), is(corProduto));
		assertThat(modalProdutoPage.obterQuantidadeProduto(), is(Integer.toString(quantidadeProduto)));
		String subTotalString = modalProdutoPage.obtersubtotal();
		subTotalString = subTotalString.replace("$", "");
		Double subtotal = Double.parseDouble(subTotalString);

		Double subtotalCalculado = quantidadeProduto * precoProduto;

		assertThat(subtotal, is(subtotalCalculado));

	}

	// Valores esperados

	String esperado_nomeProduto = "Hummingbird printed t-shirt";
	Double esperado_precoProduto = 19.12;
	String esperado_tamanhoProduto = "M";
	String esperado_corProduto = "Black";
	int esperado_input_quantidadeProduto = 2;
	Double esperado_subtotalProduto = esperado_precoProduto * esperado_input_quantidadeProduto;

	int esperado_numeroItensTotal = esperado_input_quantidadeProduto;
	Double esperado_subtotalTotal = esperado_subtotalProduto;
	Double esperado_shippingTotal = 7.00;
	Double esperado_totalTaxExclTotal = esperado_subtotalProduto + esperado_shippingTotal;
	Double esperado_totalTaxIncTotal = esperado_totalTaxExclTotal;
	Double esperado_taxesTotal = 0.00;

	String esperandonomeCliente = "Marcelo Bittencourt";
	CarrinhoPage carrinhoPage;

	@Test

	public void testIrParaCarrinho_InformacoesPersistidas() {
		// --pr� - condi��es
		// produto incluido na tela ModalProdutoPage
		testIncluirProdutoNoCarrinho_ProdutoIncluidoComSucesso();

		carrinhoPage = modalProdutoPage.clicarBotaoProceedToCheckout();

		// teste
		// Validar todos os elementos da tela

		System.out.println("***TELA DO CARRINHO***");

		System.out.println(carrinhoPage.obter_nomeProduto());
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_precoProduto()));
		System.out.println(carrinhoPage.obter_tamanhoProduto());
		System.out.println(carrinhoPage.obter_corProduto());
		System.out.println(carrinhoPage.obter_input_quantidadeProduto());
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalProduto()));

		System.out.println("***ITENS DE TOTAIS***");

		System.out.println(Funcoes.removeTextoItemsDevolveInt(carrinhoPage.obter_numeroItensTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_shippingTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxExclTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxIncTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_taxesTotal()));

		// Asser�oes Hamcrest
		assertThat(carrinhoPage.obter_nomeProduto(), is(esperado_nomeProduto));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_precoProduto()), is(esperado_precoProduto));
		assertThat(carrinhoPage.obter_tamanhoProduto(), is(esperado_tamanhoProduto));
		assertThat(carrinhoPage.obter_corProduto(), is(esperado_corProduto));
		assertThat(Integer.parseInt(carrinhoPage.obter_input_quantidadeProduto()),
				is(esperado_input_quantidadeProduto));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalProduto()),
				is(esperado_subtotalProduto));

		assertThat(Funcoes.removeTextoItemsDevolveInt(carrinhoPage.obter_numeroItensTotal()),
				is(esperado_numeroItensTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalTotal()), is(esperado_subtotalTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_shippingTotal()), is(esperado_shippingTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxExclTotal()),
				is(esperado_totalTaxExclTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxIncTotal()),
				is(esperado_totalTaxIncTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_taxesTotal()), is(esperado_taxesTotal));

		// Asser��o JUnit

		/*
		 * 
		 * assertThat(carrinhoPage.obter_nomeProduto(), is(esperado_nomeProduto));
		 * assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_precoProduto(
		 * )), is(esperado_precoProduto));
		 * assertThat(carrinhoPage.obter_tamanhoProduto(), is(esperado_tamanhoProduto));
		 * assertThat(carrinhoPage.obter_corProduto(), is(esperado_corProduto));
		 * assertThat(Integer.parseInt(carrinhoPage.obter_input_quantidadeProduto()),
		 * is(esperado_input_quantidadeProduto));
		 * assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.
		 * obter_subtotalProduto()), is(esperado_subtotalProduto));
		 * 
		 * 
		 * assertThat(Funcoes.removeTextoItemsDevolveInt(carrinhoPage.
		 * obter_numeroItensTotal()), is(esperado_numeroItensTotal));
		 * assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalTotal
		 * ()), is(esperado_subtotalTotal));
		 * assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_shippingTotal
		 * ()), is(esperado_shippingTotal));
		 * assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.
		 * obter_totalTaxExclTotal()), is(esperado_totalTaxExclTotal));
		 * assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.
		 * obter_totalTaxIncTotal()), is(esperado_totalTaxIncTotal));
		 * assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_taxesTotal())
		 * , is(esperado_taxesTotal));
		 */

	}

	CheckoutPage checkoutPage;

	@Test

	public void testIrParaCheckout_freteMeioPagamentoEnderecoListadosOk() {

		// Pr� - Condi��es

		// Produto disponivel no carrinho de compra
		testIrParaCarrinho_InformacoesPersistidas();

		// Teste

		// Clicar no Bot�o

		checkoutPage = carrinhoPage.clicarBotaoProceedToCheckout();

		// Preencher Informa��es

		// Validar Informa��es na Tela
		assertThat(Funcoes.removeCifraoDevolveDouble(checkoutPage.obter_totalTaxIncTotal()),
				is(esperado_totalTaxIncTotal));
		// assertThat(checkoutPage.obter_nomeCliente(), is(esperandonomeCliente));
		assertTrue(checkoutPage.obter_nomeCliente().startsWith(esperandonomeCliente));

		checkoutPage.clicarBotaoContinueAddress();

		String encontrado_shippingValor = checkoutPage.obter_shippingValor();
		encontrado_shippingValor = Funcoes.removeTexto(encontrado_shippingValor, " tax excl.");
		Double encontrado_shippingValor_Double = Funcoes.removeCifraoDevolveDouble(encontrado_shippingValor);
		assertThat(encontrado_shippingValor_Double, is(esperado_shippingTotal));

		checkoutPage.clicarBotaoContinueShipping();

		// Selecionar Op��o "Pay by Check"
		checkoutPage.selecionarRadioPayByCheck();

		// Validar Valor do chegue
		String encontrado_amountPayCheck = checkoutPage.Obter_amountPayByCheck();
		encontrado_amountPayCheck = Funcoes.removeTexto(encontrado_amountPayCheck, " (tax incl.)");
		Double encontrado_amountPayCheck_Double = Funcoes.removeCifraoDevolveDouble(encontrado_amountPayCheck);
		assertThat(encontrado_amountPayCheck_Double, is(esperado_totalTaxIncTotal));

		// Clicar na op��o "I agree"
		checkoutPage.selecionarCheckboxIgree();

		assertTrue(checkoutPage.estaSelecionadoCheckboxIgree());
	}

	@Test

	public void testFinalizarPedido_pedidoFinalizadoComSucesso() {
		// Pr�-Condic�o
		testIrParaCheckout_freteMeioPagamentoEnderecoListadosOk();
		// Checkout Totalmente Conclu�do

		// Teste

		// Clicar no Bot�o Para Confirmar Pedido
		PedidoPage pedidoPage = checkoutPage.clicarBotaoConfirmaPedido();
		// Validar Valores na Tela
		assertTrue(pedidoPage.obter_textoPedidoConfirmado().endsWith("YOUR ORDER IS CONFIRMED"));
		// assertThat( pedidoPage.obter_textoPedidoConfirmado().toUpperCase(), is("YOUR
		// ORDER IS CONFIRMED"));
		assertThat(pedidoPage.obter_email(), is(" marcelo@teste.com"));
		assertThat(pedidoPage.obter_totalProdutos(), is(esperado_subtotalProduto));
		assertThat(pedidoPage.obter_totalTaxIncl(), is(esperado_totalTaxIncTotal));
		assertThat(pedidoPage.obter_metodoPagamento(), is(" check"));

	}

}
