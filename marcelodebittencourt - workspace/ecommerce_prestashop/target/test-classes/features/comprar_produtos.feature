#language:pt
Funcionalidade: Comprar produtos
  Como um usuario logado
  e visualizar esse produto no carrinho
  para concluir o pedido

  @validacaoinicial
  Cenario: Deve mostrar uma lista de oito produtos na pagina inicial
    Dado que estou na pagina inicial
    Quando não estou logado
    Entao visualizo 8 produtos disponivel
    E carrrinho esta zerado

  @fluxopadrao
  Esquema do Cenario: Deve mostrar produto escolhido confirmar
    Dado que estou na pagina inicial
    Quando estou logado
    E seleciono um produto na posicao <posicao>
    E nome do produto na tela principal e na tela produto eh <nomeProduto>
    E preco do produto na tela principal e na tela produto eh <precoProduto>
    E adiciono o produto no carrinho com o tamanho <tamanhoProduto> cor <corProduto> e quantidade <quantidadeProduto>
    Entao o produto aparece na configuracao com nome <nomeProduto> preco <precoProduto> tamanho <tamanhoProduto> cor <corProduto> e quantidade <quantidadeProduto>

    Exemplos: 
      | posicao | nomeProduto                   | precoProduto | tamanhoProduto | corProduto | quantidadeProduto |
      |       0 | "Hummingbird Printed T-Shirt" | "$19.12"     | "M"            | "Black"    |                 2 |
      |       1 | "Hummingbird Printed Sweater" | "$28.72"     | "XL"           | "N/A"      |                 3 |
