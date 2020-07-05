package steps;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.google.common.io.Files;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import pages.HomePage;
import pages.LoginPage;
import pages.ModalProdutoPage;
import pages.ProdutoPage;

public class ComprarProdutosSteps {
	private static WebDriver driver;
	private HomePage homePage = new HomePage(driver);

	@Before
	public static void inicializar() {
		System.setProperty("webdriver.chrome.driver", "C:\\Drivers\\chrome83\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}

	@Dado("que estou na pagina inicial")
	public void que_estou_na_pagina_inicial() {

		homePage.carregarPagina_Inicial();
		assertThat(homePage.obterTituloPagina(), is("Loja de Teste"));
	}

	@Quando("não estou logado")
	public void não_estou_logado() {
		assertThat(homePage.estaLogado(), is(false));

	}

	@Entao("visualizo {int} produtos disponivel")
	public void visualizo_produtos_disponivel(Integer int1) {

		assertThat(homePage.contarProdutos(), is(int1));
	}

	@Entao("carrrinho esta zerado")
	public void carrrinho_esta_zerado() {

		assertThat(homePage.obterQuantidadeProdutosNoCarrinho(), is(0));
	}

	LoginPage loginPage;

	@Quando("estou logado")
	public void estou_logado() {
		// clicar no botão Sign In na home page

		loginPage = homePage.clicarBotaoSignIn();

		// Preencher usuario e senha
		loginPage.preencherEmail("marcelo@teste.com");
		loginPage.preencherPassord("marcelo");

		// clicar no bot�o Sign para logar
		loginPage.clicarBotaoSingIn();

		// Validar se o usuario est� logado de fato
		assertThat(homePage.estaLogado("Marcelo Bittencourt"), is(true));
		homePage.carregarPagina_Inicial();
	}

	ProdutoPage produtoPage;
	String nomeProdutoHomePage;
	String precoProdutoHomePage;
	String nomeProdutoPage;
	String precoProdutoPage;

	@Quando("seleciono um produto na posicao {int}")
	public void seleciono_um_produto_na_posicao(Integer indice) {
		nomeProdutoHomePage = homePage.obterNomeProduto(indice);
		precoProdutoHomePage = homePage.obterPrecoProduto(indice);

		System.out.println(nomeProdutoHomePage);
		System.out.println(precoProdutoHomePage);

		produtoPage = homePage.clicarProduto(indice);

		nomeProdutoPage = produtoPage.obterNomeProduto();
		precoProdutoPage = produtoPage.obterPrecoProduto();
	}

	@Quando("nome do produto na tela principal e na tela produto eh {string}")
	public void nome_do_produto_na_tela_principal_eh(String nomeProduto) {
		assertThat(nomeProdutoHomePage.toUpperCase(), is(nomeProduto.toUpperCase()));
		assertThat(nomeProdutoPage.toUpperCase(), is(nomeProduto.toUpperCase()));

	}

	@Quando("preco do produto na tela principal e na tela produto eh {string}")
	public void preco_do_produto_na_tela_principal_eh(String precoProduto) {
		assertThat(precoProdutoHomePage, is(precoProduto.toUpperCase()));
		assertThat(precoProdutoPage, is(precoProduto.toUpperCase()));

	}

	ModalProdutoPage modalProdutoPage;

	@Quando("adiciono o produto no carrinho com o tamanho {string} cor {string} e quantidade {int}")
	public void adiciono_o_produto_no_carrinho_com_o_tamanho_cor_e_quantidade(String tamanhoProduto, String corProduto,
			Integer quantidadeProduto) {

		// Selecionar tamanho
		List<String> listaOpcoes = produtoPage.obterOpcoesSelecionadas();

		System.out.println(listaOpcoes.get(0));
		System.out.println("Tamanho da lista: " + listaOpcoes.size());

		produtoPage.selecionarOpcaoDropDown(tamanhoProduto);

		listaOpcoes = produtoPage.obterOpcoesSelecionadas();

		System.out.println(listaOpcoes.get(0));
		System.out.println("Tamanho da lista: " + listaOpcoes.size());

		// Selecionar Cor
		if (!corProduto.equals("N/A"))
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

	}

	@Entao("o produto aparece na configuracao com nome {string} preco {string} tamanho {string} cor {string} e quantidade {int}")
	public void o_produto_aparece_na_configuracao_com_nome_preco_tamanho_cor_e_quantidade(String nomeProduto,
			String precoProduto, String tamanhoProduto, String corProduto, Integer quantidadeProduto) {

		assertThat(modalProdutoPage.obterDescricaoProduto().toUpperCase(), is(nomeProdutoPage.toUpperCase()));

		Double precoProdutoEncontrado = Double.parseDouble(modalProdutoPage.obterPrecoProduto().replace("$", ""));
		Double precoProdutoEsperado = Double.parseDouble(precoProduto.replace("$", ""));

		assertThat(modalProdutoPage.obterTamanhoProduto(), is(tamanhoProduto));
		if (!corProduto.equals("N/A"))
			assertThat(modalProdutoPage.obterCorProduto(), is(corProduto));
		assertThat(modalProdutoPage.obterQuantidadeProduto(), is(Integer.toString(quantidadeProduto)));

		String subTotalString = modalProdutoPage.obtersubtotal();
		subTotalString = subTotalString.replace("$", "");

		Double subtotalEncontrado = Double.parseDouble(subTotalString);

		Double subtotalCalculadoEsperado = quantidadeProduto * precoProdutoEsperado;

		assertThat(subtotalEncontrado, is(subtotalCalculadoEsperado));

	}

	@After (order = 1)
	public void capturarTela(Scenario scenario) {
		TakesScreenshot camera = (TakesScreenshot) driver;
		File capturaDeTela = camera.getScreenshotAs(OutputType.FILE);
		System.out.println(scenario.getId());	
		
		String scenarioId = scenario.getId().substring(scenario.getId().lastIndexOf(".feature:") + 9);
		
		String nomeArquivo = "resources/screenshots/" + scenario.getName() + "_" +scenarioId + "_"+ scenario.getStatus() + ".png";
		System.out.println(nomeArquivo);
		
		try {
				 Files.move(capturaDeTela, new File	(nomeArquivo)); 
				 
			} catch (IOException e) {
			
			e.printStackTrace();	
		}
	}

		@After (order = 0)
		public static void finalizar() {
			driver.quit();
		}

	

}
