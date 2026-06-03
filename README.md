# Gerenciamento de Memória - Sistemas Operacionais – Trabalho Prático 2

 O presente trabalho tem por objetivo explorar as abordagens de alocação de espaço contíguo para
processos, e explorar as diferentes técnicas e políticas de alocação de espaços em uma memória.

## Estratégias de Alocação
A ferramenta deverá permitir a exploração de (i) partições variáveis ou (ii) partições definidas com o
sistema buddy. Comum a todos os métodos deverá ser a informação do tamanho da memória principal a ser
empregada, que define o tamanho total inicial disponível para alocação. Deverá ser assumido um tamanho
sempre equivalente a uma potência de dois.

Para o sistema de partições variáveis, deverá ser possível informar a política de alocação a ser
empregada, se Worst-Fit ou Circular-Fit. A escolha do tipo de política a ser aplicada deverá ser realizada em
tempo de execução pelo usuário. Diferente do mecanismo de particionamento variável, para o sistema buddy,
nada além da requisição de alocação de memória e liberação de memória dos processos deverá ser informado.

No escopo deste trabalho, o tratamento de alocações de processos que venham a ultrapassar a
quantidade de espaço disponível na memória principal, deverá se dar a partir da notificação de “ESPAÇO
INSUFICIENTE DE MEMÓRIA”.

## Sistema Buddy

O sistema buddy aloca memória a partir de um segmento de tamanho fixo que consiste em páginas
fisicamente contíguas. A memória é alocada nesse segmento usando um alocador que atende às solicitações
em unidades dimensionadas como potência de 2 (4 KB, 8 KB, 16 KB e assim por diante). Uma solicitação em
unidades não dimensionadas adequadamente é arredondada para a próxima potência mais alta de 2. Por
exemplo, uma solicitação de 11 KB é satisfeita com um segmento de 16 KB.

Por exemplo, suponha que o tamanho de um segmento de memória seja inicialmente de 256 KB e que
o kernel solicite 21 KB de memória. O segmento é inicialmente dividido em dois buddies — AL e AR — cada
um com 128 KB de tamanho. Um desses buddies é dividido em dois buddies de 64 KB – BL e BR. No entanto,
a próxima maior potência de 2 de 21 KB é 32 KB, portanto BL ou BR são novamente divididos em dois buddies
de 32 KB, CL e CR. Um desses buddies é usado para satisfazer a solicitação de 21 KB. Este esquema é ilustrado
na figura 1, onde CL é o segmento alocado para a solicitação de 21 KB.

Uma vantagem do sistema buddy é a rapidez com que buddies adjacentes podem ser combinados para
formar segmentos maiores usando uma técnica conhecida como coalescência. Na figura 1, por exemplo,
quando o kernel libera a unidade CL que foi alocada, o sistema pode unir CL e CR em um segmento de 64 KB.
Este segmento, BL, pode, por sua vez, ser unido ao seu companheiro BR para formar um segmento de 128 KB.
No final das contas, podemos acabar com o segmento original de 256 KB.

## Requisições de alocação de processos
A sequência de requisições de alocação e liberação de espaços de memória deverá ser informada em
um arquivo. Uma requisição de alocação de espaço em memória deverá ser realizada a partir da representação
do comando (“IN”), do nome do processo (“ID”) e do tamanho do processo. Para o pedido de liberação de um
espaço, ou seja, quando um processo deixa de existir na memória, um comando (“OUT”) e a identificação
deste (“ID) deverão ser informados.

## Visualização de resultados
Da escolha da estratégia de alocação para o tipo de particionamento escolhido e do arquivo com a
descrição das sequências de requisições, deverá ser possível visualizar os espaços livres para alocação. A
execução da ferramenta deverá permitir a visualização a cada passo.

Para a abordagem do sistema buddy, o total de fragmentação interna deverá ser apresentado.

Comum a todas as abordagens deverá ser a listagem do total de blocos contíguos livres para alocação
a cada linha do arquivo lido.

Exemplo: Assuma uma memória de 16 posições, que emprega particionamento variável com a política
de alocação worst-fit. Assuma ainda o seguinte arquivo de descrição de requisições, e o respectivo
detalhamento da ocupação da memória.

## Informações adicionais
O trabalho deverá ser realizado em grupos de 4 alunos (obrigatoriamente). Deverá ser entregue o
código fonte do programa desenvolvido, bem como um manual do usuário em formato PDF contendo as
explicações de como compilar e executar o programa. A linguagem de programação utilizada para desenvolver
o trabalho é de escolha do grupo, desde que seja possível compilar e executar o programa no ambiente
computacional disponível nas salas de aula laboratório do prédio 32.

O trabalho deverá ser entregue no moodle a partir de um arquivo compactado (.zip). O nome do arquivo
deverá ser tal que contenha o nome e sobrenome de todos os integrantes do grupo. O material postado no
moodle é de inteira responsabilidade do aluno. A presença de arquivos corrompidos, que impeçam a avaliação
do trabalho pelo professor será considerada como a não entrega do trabalho. Também não serão considerados
trabalhos com erros de compilação. Casos em que sejam identificados plágio/cópia ou realizados por sistemas
de IA (por exemplo, ChatGPT, Gemini, Copilot e outros), receberão nota zero.

A data de entrega está prevista para o dia 02/07/2026, até às 17hs. As apresentações serão realizadas
pelos alunos a partir do material postado no Moodle.

Cada aluno deverá assinalar no Moodle o grupo ao qual pertence e um integrante do grupo deverá
escolher um horário para o grupo apresentar o trabalho no dia 02/07/2026 ou no dia 07/07/2026, dentre os
horários disponíveis no Moodle.
