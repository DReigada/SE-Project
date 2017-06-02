Política otimista quer dizer que não há locks na base de dados. É assumido que múltiplas transações podem frequentemente ser completadas sem interferirem umas com as outras.
Mais concretamente, uma transação antes de fazer commit verifica se nenhuma outra transação modificou os dados que leu. Se não, esta prossegue com o commit. Por outro lado, se houver conflito, a transação faz rollback e pode recomeça do início.

Esta política é geralmente utilizada em ambiente com pouca quantidade de dados, onde os conflitos são raros e as transações se podem completar sem a despesa extra dos locks. Num ambiente de serviço remoto há muitos dados e os conflitos são frequentes, os custos de performance por recomeçar repetidamente as transações é muito elevado, logo concluimos que esta política, utilizada pela FénixFramework, não é ideal para o nosso caso.

Como esperado, com o aumento do número de users a fazer pedidos em simultâneo ao servidor o tempo de resposta aumenta de forma não linear. Por exemplo, no test 30writes para 10 utilizadores o tempo de resposta médio do processamento de uma adventure é por volta de 800ms, enquanto que para os 2000 utilizadores é mais de 1 minuto.

O que não estávamos à espera foi da ocorrência de tantos erros com o aumento da load sobre o servidor. No mesmo teste do exemplo referido anteriormente, com 10 utilizadores não se verificava qualquer erro na resposta, mas para 2000 utilizadores já 60% dos pedidos efetuados tinham erro. 

Achamos que este mau desempenho verificado se deve à política otimista usada pela FenixFramework, porque, como referido anteriormente, as características deste projeto não se alinham com aquelas onde a política otimista traz benefícios.