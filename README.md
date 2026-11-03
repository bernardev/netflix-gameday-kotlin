# Netflix Gameday | Trabalho 4: Arquitetura MVVM

App Android em Kotlin + Jetpack Compose que imita a Netflix. No Trabalho 3, os **perfis** e os **filmes** passaram a ficar salvos em um banco **Room** (SQLite) no próprio aparelho. No Trabalho 4 o app continua com as mesmas telas e as mesmas funções, mas o código foi reorganizado em camadas no padrão **MVVM** (Model, View, ViewModel), com **Repository**, **StateFlow/UiState** e **ViewModelFactory**.

> Disciplina: Desenvolvimento de Aplicativos Móveis. Trabalho 4 de 4 da A2 (continuação do Trabalho 3).
>
> A documentação do Trabalho 4 está na seção [Arquitetura MVVM (Trabalho 4)](#arquitetura-mvvm-trabalho-4). As seções do Trabalho 3 continuam mais abaixo.

---

## Como rodar o projeto

1. No **Android Studio**: *File > Open* e escolha a pasta raiz deste repositório (a que tem `settings.gradle.kts`).
2. Espere o **Gradle Sync** terminar. O Android Studio baixa o Gradle e as bibliotecas sozinho.
3. Rode o app em um emulador ou celular (botão ▶ Run).

Estrutura do projeto (modelo *Empty Activity*, pacote `com.example.netflix`):

```
settings.gradle.kts
build.gradle.kts              <- plugins do projeto (com o KSP)
gradle/libs.versions.toml     <- versões usadas pelos alias(libs...)
app/build.gradle.kts          <- módulo: Compose, Navigation e Room
app/src/main/AndroidManifest.xml
app/src/main/java/com/example/netflix/
    MainActivity.kt           <- ponto de entrada; cria os Repositories e chama AppNavigation(...)
    data/                     <- Entities, DAOs e AppDatabase (Room)
    data/repository/          <- FilmeRepository e PerfilRepository
    viewmodel/                <- ViewModels, UiState e Factories
    ui/                       <- telas (@Composable), NavHost e componentes visuais
app/src/main/res/drawable/    <- capas e fotos de perfil
```

> **Se o Sync reclamar de versão** (AGP, Kotlin ou Gradle mais novo/antigo que o Android Studio da máquina): aceite a sugestão de atualização do próprio Android Studio, ou crie um projeto *Empty Activity* novo nessa máquina e copie para cá o `gradle/libs.versions.toml` e o `gradle/wrapper/gradle-wrapper.properties` dele. O `app/build.gradle.kts` não muda.

### Configuração do Room no Gradle (mesma ordem vista em aula)

**Passo 1 — `build.gradle.kts` do projeto:** adicione o plugin do KSP e sincronize *antes* de mexer no módulo.

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.devtools.ksp") version "2.3.4" apply false
}
```

> A versão do KSP precisa ser compatível com a versão do Kotlin do projeto (veja o aviso no slide "Adicionar o plug-in KSP ao projeto"). `2.3.4` é a versão usada no exemplo da aula.

**Passo 2 — `build.gradle.kts` do módulo (`app`):** ative o KSP nos `plugins` e sincronize.

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}
```

**Passo 3 — ainda no módulo:** adicione as dependências do Room em `dependencies` e sincronize de novo.

```kotlin
val room_version = "2.8.0"

implementation("androidx.room:room-runtime:$room_version")
implementation("androidx.room:room-ktx:$room_version")
ksp("androidx.room:room-compiler:$room_version")
```

> `2.8.0` é a versão mostrada no slide da página de versões do Room. O outro slide da aula usa `2.6.1`, mas só para o Android Studio 2022.3.1. Com o Android Studio atual, use a `2.8.0`.

As dependências que já vinham do Trabalho 2 continuam no `app/build.gradle.kts`: Navigation Compose `2.10.2` e `material-icons-core:1.7.8` (como no `build.gradle.kts` da Aula 13) e `lifecycle-viewmodel-compose:2.11.0` (para o `viewModel()`, como no LEIA-ME da Aula 13).

---

## Arquitetura MVVM (Trabalho 4)

O Trabalho 4 não adiciona tela nem funcionalidade. O app faz exatamente o que já fazia no Trabalho 3 (adicionar, listar, atualizar, remover e persistir), só que agora cada parte do código tem uma responsabilidade só.

```
com.example.netflix/
    MainActivity.kt                    <- abre o banco, cria os Repositories e chama AppNavigation(...)
    data/
        Filme.kt, Perfil.kt            <- @Entity
        FilmeDAO.kt, PerfilDAO.kt      <- @Dao (buscarTodos() agora retorna Flow)
        AppDatabase.kt                 <- @Database em Singleton
    data/repository/
        FilmeRepository.kt             <- única porta de entrada para os dados de filmes
        PerfilRepository.kt            <- única porta de entrada para os dados de perfis
    viewmodel/
        FilmeViewModel.kt   FilmeUiState.kt   FilmeViewModelFactory.kt
        PerfilViewModel.kt  PerfilUiState.kt  PerfilViewModelFactory.kt
    ui/
        AppNavigation.kt               <- NavHost; cria os ViewModels com viewModel(factory = ...)
        TelaPerfis, TelaInicio, TelaBuscar, TelaFilme, TelaDownloads, TelaMenu, TelaEditarPerfil
        Componentes.kt, Imagens.kt     <- cores, botões, barras e fotos reutilizados pelas telas
```

**Caminho de uma ação** (ex.: adicionar um filme na tela Buscar):

```
TelaBuscar  --filmeViewModel.adicionar()-->  FilmeViewModel  --viewModelScope.launch-->  FilmeRepository.inserir()  -->  FilmeDAO.inserir()  -->  Room
```

**Caminho de volta** (a lista na tela se atualiza sozinha):

```
Room  --Flow<List<Filme>>-->  FilmeRepository.buscarTodos()  -->  FilmeViewModel (_uiState)  --uiState: StateFlow-->  TelaBuscar (collectAsState)
```

### Onde está cada item do checklist

| Item do checklist | Onde está |
|---|---|
| Código em 4 pacotes: `ui`, `data`, `data.repository`, `viewmodel` | pastas `ui/`, `data/`, `data/repository/`, `viewmodel/` |
| 1 Repository por Entity, e só ele fala com o DAO | `FilmeRepository(filmeDAO)` e `PerfilRepository(perfilDAO)` |
| `buscarTodos()` do DAO retorna `Flow<List<...>>` | `FilmeDAO.buscarTodos()`, `FilmeDAO.buscarBaixados()` e `PerfilDAO.buscarTodos()` |
| 2 ViewModels, cada um com seu UiState | `FilmeViewModel` + `FilmeUiState`, `PerfilViewModel` + `PerfilUiState` |
| `_uiState` privado + `uiState` público (`StateFlow`) | início de cada ViewModel |
| 1 Factory por ViewModel, injetando o Repository | `FilmeViewModelFactory` e `PerfilViewModelFactory` |
| Telas sem `dao`, `CoroutineScope` ou `Dispatchers.IO` | pasta `ui/`: as telas só leem `uiState` e chamam métodos do ViewModel |

### Como estava o projeto no Trabalho 3 e o que mudou

| Antes (Trabalho 3) | Agora (Trabalho 4) |
|---|---|
| Todos os 18 arquivos soltos no mesmo pacote `com.example.netflix` | Arquivos separados nos pacotes `data`, `data.repository`, `viewmodel` e `ui` |
| Cada tela abria o banco com `AppDatabase.getDatabase(context)` e passava o DAO para o ViewModel (`filmeViewModel.adicionar(busca, filmeDAO)`) | A tela não sabe que existe banco. Só chama `filmeViewModel.adicionar()` |
| A `TelaFilme` buscava o filme com `CoroutineScope(Dispatchers.IO).launch { ... }` dentro do `@Composable` | A `TelaFilme` acha o filme pelo id na lista `filmes` do `FilmeUiState` |
| Os ViewModels usavam `CoroutineScope(Dispatchers.IO)`, que não é cancelado quando o ViewModel some | Os ViewModels usam `viewModelScope.launch`, que é cancelado junto com o ViewModel |
| `buscarTodos()` era `suspend` e devolvia `List`. Depois de cada inserir/atualizar/deletar o código tinha que buscar tudo de novo | `buscarTodos()` devolve `Flow`. O Room avisa sozinho quando a tabela muda |
| Cada tela carregava os dados ao abrir, com `LaunchedEffect(Unit)` | O ViewModel começa a observar o banco uma vez, no `init`, e todas as telas recebem a lista pronta |
| Estado espalhado: vários `mutableStateOf` no ViewModel e `remember` nas telas (texto da busca, novo perfil, novo nome) | Um `data class` de UiState por ViewModel, com tudo que as telas precisam |
| A `TelaFilme` decidia se o perfil era admin percorrendo a lista `nomesAdmin` | O `PerfilViewModel` calcula `ehAdmin` e a tela só lê `uiState.ehAdmin` |
| Funções soltas de acesso ao banco (`buscarFilmes`, `inserirFilme`...) com `try/catch` no fim dos ViewModels | Os mesmos `try/catch` + `Log.e` agora ficam dentro do Repository |

### Como o trio decidiu dividir os pacotes

- **`data`**: as duas `@Entity` (`Filme`, `Perfil`), os dois `@Dao` e o `AppDatabase`. Foram só movidos; continuam do jeito do Trabalho 3 (a única mudança é o `Flow`, seção abaixo).
- **`data.repository`**: `FilmeRepository` e `PerfilRepository`. Ficam dentro de `data` porque fazem parte da camada de dados, mas em um pacote próprio porque não são Room.
- **`viewmodel`**: para cada ViewModel ficaram juntos os três arquivos que trabalham juntos: o `ViewModel`, o `UiState` e a `Factory`.
- **`ui`**: as 7 telas, o `AppNavigation` (é ele que monta as telas) e os arquivos `Componentes.kt` e `Imagens.kt`, que só desenham coisas na tela.
- **`MainActivity`** ficou na raiz: é o ponto de entrada do app (o `AndroidManifest` aponta para ela) e é o único lugar fora de `data` que abre o `AppDatabase`, só para criar os Repositories.

Decisão importante: os dois ViewModels continuam sendo criados **uma vez só, no `AppNavigation`**, e passados para as telas (igual ao Trabalho 3). O perfil escolhido aparece em todas as telas (barra inferior, "Para lau" no Início, área de admin na tela do filme). Se cada tela criasse o seu próprio ViewModel, cada uma teria um perfil atual diferente.

### Por que o Repository existe

Antes, quem chamava o DAO eram as telas e os ViewModels, e cada um precisava saber que os dados vinham do Room. Agora:

```
Tela  ->  ViewModel  ->  Repository  ->  DAO
```

- A tela nunca mais fala com o DAO. Só o Repository fala com o DAO, e só o ViewModel fala com o Repository.
- Se um dia os filmes vierem de outro lugar além do Room (uma API, por exemplo), só o `FilmeRepository` muda. ViewModel e telas continuam iguais.
- O tratamento de erro (`try/catch` + `Log.e`) e a carga inicial (gravar os filmes e perfis do Trabalho 2 quando o banco está vazio) ficaram num lugar só, em vez de espalhados pelos ViewModels.

### DAO reativo com Flow

```kotlin
@Query("SELECT * FROM filmes")
fun buscarTodos(): Flow<List<Filme>>
```

Sem `suspend`, porque o `Flow` já é assíncrono. O ViewModel começa a observar esse `Flow` no `init` e, sempre que um filme é inserido, renomeado, baixado ou excluído, o Room manda a lista nova sozinho. Por isso sumiram todos os "buscar de novo depois de alterar" e todos os `LaunchedEffect(Unit)` de carga das telas.

O mesmo vale para `buscarBaixados()` (tela de Downloads) e `PerfilDAO.buscarTodos()`. Também criamos `contar()` nos dois DAOs, para o Repository saber se o banco está vazio e gravar os itens iniciais. A consulta `buscarPorId(id)` do Trabalho 3 continua no DAO, mas a `TelaFilme` não precisa mais dela: o filme já está na lista observada.

### O que tem dentro do UiState de cada tela

**`FilmeUiState`** (usado por Início, Buscar, Filme e Downloads)

| Campo | Por quê |
|---|---|
| `filmes: List<Filme>` | Lista da tela Buscar, contador "Todos os filmes (8)" e a tela do filme acha o filme por aqui |
| `baixados: List<Filme>` | Lista e contador da tela Downloads |
| `busca: String` | Texto digitado no campo "Buscar ou adicionar". Antes era um `remember` na tela |
| `novoNome: String` | Texto do campo "Novo nome do filme" na área do administrador. Antes era um `remember` na tela |

**`PerfilUiState`** (usado por todas as telas, por causa da barra inferior)

| Campo | Por quê |
|---|---|
| `perfis: List<Perfil>` | Lista da tela Perfis e contador "6 perfis nesta conta" no Menu |
| `perfilAtual: String` e `idPerfilAtual: Int` | Nome mostrado nas telas e id usado para renomear e excluir a linha certa do banco |
| `ehAdmin: Boolean` | Mostra ou esconde a área do administrador na tela do filme. Antes a própria tela fazia essa conta |
| `novoPerfil: String` | Texto do campo "Novo perfil". Antes era um `remember` na tela |
| `nomeEditado: String` | Texto do campo "Nome do perfil" em Editar perfil. Antes era um `remember` na tela |
| `proximoEpisodio`, `previas: Boolean` | Os dois checkboxes de Editar perfil (continuam só na memória, como no Trabalho 3) |

Os campos de texto foram para o UiState porque o enunciado pede "tudo que aquela tela precisa para se desenhar" num lugar só. Assim a tela fica só com `uiState.xxx` para ler e `viewModel.xxx()` para chamar.

Em cada ViewModel o padrão é o mesmo:

```kotlin
private val _uiState = MutableStateFlow(FilmeUiState())
val uiState: StateFlow<FilmeUiState> = _uiState.asStateFlow()
```

`_uiState` é privado: só o ViewModel altera. `uiState` é público e só leitura: a tela observa com `val filmeUiState by filmeViewModel.uiState.collectAsState()`.

### ViewModelFactory

O `FilmeViewModel` agora recebe o `FilmeRepository` no construtor, e o Android não sabe criar sozinho um ViewModel com parâmetro. Por isso cada ViewModel tem uma `Factory` (`ViewModelProvider.Factory`) que entrega o Repository certo na hora da criação. No `AppNavigation`:

```kotlin
val filmeViewModel: FilmeViewModel = viewModel(factory = FilmeViewModelFactory(filmeRepository))
```

### Alguma funcionalidade quebrou durante a reorganização?

- **Fotos sumiram do código (erro de compilação):** o `Imagens.kt` usa `R.drawable...`. A classe `R` é gerada no pacote `com.example.netflix`, então ao mover o arquivo para `ui` ele deixou de achar o `R`. Corrigido com `import com.example.netflix.R`.
- **Tela do filme desatualizada:** no Trabalho 3 a `TelaFilme` guardava uma cópia do filme e, depois de renomear ou baixar, atualizava essa cópia na mão (`filme = Filme(filme.id, novoNome, ...)`). Agora a tela lê o filme da lista `filmes` do UiState, que vem do `Flow`. Quando o Room grava, a tela muda sozinha, e essas linhas foram removidas.
- **Texto do Editar perfil:** antes o campo começava com o nome do perfil porque o `remember` era criado toda vez que a tela abria. Com o texto no UiState, ele ficaria com o que foi digitado da última vez. Corrigido com `comecarEdicao()`, chamado pelo botão "Editar perfil" do Menu, que copia o nome atual para `nomeEditado`.
- **Projeto não sincronizava:** o `settings.gradle.kts` estava com `includeGroupByRegex("com\.android.*")`, e o Kotlin não aceita `\.` dentro de texto. Corrigido para `"com\\.android.*"` (o mesmo vale para `com.google`).

> ✏️ **PREENCHER PELO TRIO:** complementem com o que aconteceu com vocês ao testar no emulador.

### Teste: o app continua fazendo tudo que fazia (prints / vídeo)

> ✏️ **PREENCHER PELO TRIO:** prints ou vídeo curto mostrando o roteiro abaixo.
>
> 1. Adicionar um perfil na tela de Perfis e um filme na tela Buscar. A lista e o contador atualizam na hora.
> 2. Entrar com o perfil Admin, abrir um filme, baixar e renomear. O selo "Baixado" e o nome mudam na mesma tela.
> 3. Ir em Downloads, ver o filme e remover o download pela lixeira.
> 4. Excluir um filme e editar/excluir um perfil.
> 5. **Fechar o app completamente** e abrir de novo: tudo continua lá.

### Algum integrante teve dificuldade em algum ponto?

**Marcos:** Quando fomos abrir o projeto na sala no Android Studio ele não sincronizava, e descobrimos que era o `settings.gradle.kts` com `"com\.android.*"`, que o Kotlin não aceita, aí trocamos para `"com\\.android.*"` e funcionou, e também depois de mover os arquivos para os pacotes, um monte de coisa ficou vermelha por falta de import, e a que mais demorou para achar foi o `Imagens.kt` que parou de encontrar o `R`, porque o `R` fica no pacote `com.example.netflix` e precisou do `import com.example.netflix.R`.

> ✏️ **PREENCHER PELOS OUTROS DOIS INTEGRANTES:** algum integrante teve dificuldade em algum ponto? Como resolveram?

---

## Estrutura do Room (Trabalho 3)

Desde o Trabalho 4, as Entities, os DAOs e o `AppDatabase` ficam no pacote `data`, e o `AppNavigation` e o `Componentes` ficam em `ui`.

| Arquivo | O que é |
|---|---|
| `Filme.kt` | `@Entity(tableName = "filmes")` com `id` (`@PrimaryKey(autoGenerate = true)`), `nome` e `baixado` |
| `Perfil.kt` | `@Entity(tableName = "perfis")` com `id` (`@PrimaryKey(autoGenerate = true)`) e `nome` |
| `FilmeDAO.kt` | `@Dao` com `inserir`, `buscarTodos`, `atualizar`, `deletar` e as consultas filtradas `buscarPorId(id)` e `buscarBaixados()` |
| `PerfilDAO.kt` | `@Dao` com `inserir`, `buscarTodos`, `atualizar` e `deletar` |
| `AppDatabase.kt` | `@Database(entities = [Filme::class, Perfil::class], version = 1)` em padrão Singleton (`companion object` + `@Volatile` + `synchronized`) |
| `AppNavigation.kt` | `NavHost` com todas as rotas do app, separado do `MainActivity` como no exemplo da Aula 13 (`MainActivity` -> `AppNavigation()`) |
| `Componentes.kt` | Cores do app, barra superior (`TopAppBar`) e botão vermelho reutilizados em todas as telas |

No Trabalho 3, todos os métodos dos DAOs eram `suspend fun` e cada chamada ao banco era feita dentro de `CoroutineScope(Dispatchers.IO).launch { ... }`. No Trabalho 4, as buscas de listas viraram `Flow`, as gravações são chamadas pelo Repository dentro de `viewModelScope.launch { ... }` e o `try/catch` + `Log.e(...)` ficou no Repository.

### Onde cada operação do CRUD acontece no app

| Operação | Onde, no app | Método do DAO |
|---|---|---|
| **Buscar** | O ViewModel observa o `Flow` desde que é criado; as telas Perfis, Início, Buscar, Menu e Downloads só leem a lista do `uiState` | `buscarTodos()` / `buscarBaixados()` |
| **Buscar por id** | Tela do filme: recebe só o id pela rota `filme/{id}` e acha o filme na lista `filmes` do `FilmeUiState` (no Trabalho 3 usava `buscarPorId(id)`) | nenhum |
| **Inserir** | Botão **Add** na tela de Perfis (novo perfil) e na tela Buscar (novo filme) | `inserir(...)` |
| **Atualizar** | **Salvar nome** na tela do filme (perfil admin), **Salvar** em Editar perfil, **Baixar** na tela do filme e a lixeira em Downloads (muda o campo `baixado`) | `atualizar(...)` |
| **Remover** | **Excluir filme** na tela do filme (perfil admin) e **Excluir perfil** em Editar perfil | `deletar(...)` |

---

## Melhorias de layout e usabilidade (pedido do professor)

Usamos só componentes vistos no material da disciplina (slides e código das aulas):

| Onde | O que mudou | Componente do material |
|---|---|---|
| Todas as telas | Paleta única: fundo preto, cartões cinza-escuro e vermelho da Netflix (`Componentes.kt`) | `Color(r, g, b)` |
| Menu, Editar perfil, Downloads e Filme | Barra superior padrão com título e botão de voltar, no lugar do cabeçalho feito à mão | `TopAppBar` + `TopAppBarDefaults.topAppBarColors` + `IconButton` |
| Botões de ação (Add, Salvar, Baixar, Excluir) | Botões no vermelho da Netflix; ações destrutivas (Excluir) em cinza, para não serem tocadas por engano | `Surface` + `clickable` + `shadowElevation` |
| Perfis, Buscar e Downloads | Itens das listas em cartões, com espaçamento uniforme; o perfil atual ganha borda vermelha | `Card` + `CardDefaults.cardColors` + `BorderStroke` + `Arrangement.spacedBy` |
| Tela do filme | Capa maior com o nome por cima; o conteúdo agora **rola** (antes, no perfil Admin, as recomendações ficavam cortadas); área do administrador separada num cartão | `LazyColumn` + `items` |
| Downloads | Contador de filmes baixados e aviso quando a lista está vazia | `Text`, `Card`, `if/else` |
| Menu | A fileira de avatares estourava a largura com muitos perfis; virou o item "Trocar de perfil" | `Column` + `Arrangement.spacedBy` |
| Barra inferior | Aba atual destacada em vermelho | `Surface` |

---

## Documentação do processo e das decisões (Trabalho 3)

### Como estava o projeto no Trabalho 2 e o que mudou

No Trabalho 2, os perfis e os filmes eram listas de `String` dentro dos ViewModels (`mutableStateListOf`). Adicionar, renomear e excluir funcionavam, mas tudo sumia quando o app era fechado. Para saber qual filme abrir, a tela de filme lia o `filmeAtual` guardado no ViewModel.

No Trabalho 3:
- os perfis e os filmes viraram **entidades do Room**, cada uma com um `id` gerado pelo banco;
- os ViewModels deixaram de ser a "fonte" dos dados e só guardam uma cópia do que foi lido do banco. Toda alteração grava no banco e depois lê a lista de novo;
- a tela do filme passou a receber **só o id pela navegação** (`filme/{id}`) e a buscar o filme no banco com `buscarPorId`;
- a lista de downloads deixou de ser uma lista separada e virou o campo `baixado` do próprio filme, lido com a consulta `buscarBaixados()`.

### Quais Entity, DAO e Database foram criados, e por que esses campos

- **`Filme`** (`id`, `nome`, `baixado`): o `nome` já existia. O campo `baixado` substitui a lista separada de downloads do Trabalho 2. Assim, "baixar" e "remover download" viram um **update** no mesmo registro, e não precisamos de uma terceira tabela só para guardar nomes repetidos.
- **`Perfil`** (`id`, `nome`): os perfis só tinham nome no Trabalho 2. As opções "reproduzir próximo episódio" e "prévias" continuam apenas na memória porque não fazem parte das listas do app.
- **`FilmeDAO`** e **`PerfilDAO`**: um DAO por entidade, com os 4 métodos básicos.
- **`AppDatabase`**: um único banco (`netflix_database`) reunindo as duas entidades, no padrão Singleton visto em aula, para não abrir várias conexões.

### Como estruturamos as queries do DAO

Além dos 4 métodos básicos, o `FilmeDAO` tem duas consultas filtradas por campo:
- `@Query("SELECT * FROM filmes WHERE id = :id") suspend fun buscarPorId(id: Int): Filme` — usada pela tela do filme;
- `@Query("SELECT * FROM filmes WHERE baixado = 1") suspend fun buscarBaixados(): List<Filme>` — usada pela tela de Downloads. O Room guarda `Boolean` como 0/1.

### Dificuldades em migrar de `mutableStateListOf` para Room e como resolvemos

- **Listas de texto viraram listas de objetos:** as telas usavam o nome do filme ou do perfil para tudo. Agora usam `it.nome` para mostrar e `it.id` para navegar, atualizar e excluir.
- **Carga inicial:** o banco começa vazio na primeira execução. Para o app abrir com os mesmos filmes e perfis do Trabalho 2, quando a busca volta vazia gravamos os itens iniciais e buscamos de novo. Consequência: se todos os itens forem excluídos, os iniciais voltam na próxima abertura.
- **Cartazes fixos:** as telas Início e Filme têm cartazes fixos por nome (Outer Banks, O Mentalista...). Como a tela do filme agora precisa de um id, criamos `idPorNome(...)` no ViewModel. Se o filme foi renomeado ou excluído, o app mostra "Filme nao encontrado" em vez de abrir uma tela vazia.
- **Conflito de nomes:** já existia uma função de tela chamada `Perfil(...)`, com o mesmo nome da nova entidade. A função de tela foi renomeada para `ItemPerfil(...)`.
- **Tela atualizada depois de editar:** quando o filme é renomeado ou baixado, a tela atualiza o próprio estado na hora, sem esperar a próxima leitura do banco.

> ✏️ **PREENCHER PELO TRIO:** complementem com o que realmente aconteceu com vocês ao rodar no Android Studio (erros de Gradle/KSP, sincronização etc.).

### Teste de persistência (prints / vídeo)

> ✏️ **PREENCHER PELO TRIO:** prints ou vídeo curto mostrando o roteiro abaixo.
>
> 1. Adicionar um filme na tela Buscar (ex.: "Duna") e um perfil na tela de Perfis.
> 2. Entrar com o perfil Admin, abrir um filme, renomear e baixar.
> 3. Excluir um filme.
> 4. **Fechar o app completamente** (tirar da lista de apps recentes) e abrir de novo.
> 5. Mostrar que o filme e o perfil adicionados, o nome alterado e o download continuam lá, e que o filme excluído não voltou.

### Dificuldades de cada integrante

> ✏️ **PREENCHER PELO TRIO:** algum integrante teve dificuldade em algum ponto? Como resolveram?

---

## O que não entra neste trabalho

O banco é só local (Room/SQLite no aparelho). Não há login, servidor, API nem sincronização na nuvem.
