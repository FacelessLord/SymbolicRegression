# SymbolicRegression

#### Annotation
Symbolic regression is a way to find mathematical formula that satisfies given conditions,
like have specified values at given arguments, have derivative equal to specified function 
(i.e. formula will be equal to antiderivative of function)

This can be very useful when you have some data set like result of experiments and want to 
find best formula to approximate those values and build new theory based on this formula

#### Details
Symbolic regression generally uses genetic algorithm to create evolution of expressions.
In such scenario fitness function is presented by sum of squares of differences between results of expression 
and values from training set.
Combinative variations presented by switching of subtrees of two trees.
Mutations are:
  * change of random operation
  * full replacement of random subtree
  * removal of some unary node in tree (this node is replaced by its child)
  * new root creation
  * shuffle of subtrees of some node
  * switch between constant and variable (separate mutation as long as these are not ordinary operations
  * replacement of full tree

#### Implementation
To incapsulate entity of expression/formula/function I use syntactic trees which have operation 
and list of subtrees which act as an operands for the operation

Syntactic tree of expression works as a chromosome of expression and provides both combinative and mutative variability.
At each simulation step half of population that have highest sum of squared differences is 
removed and every tree in other half is mutated and then combined pair by pair to restore population size.

After population change the minimal deviation is evaluated. The tree that have this deviation is saved as minimal tree.
If after given maximum generation count there no expression that satisfies condition then minimal tree is returned.

#### Features
Along with regression operators this project provides expression parsers which allow you store your expression in 
compact form and restore their functionality at need.

[NYFI] In order to make expression look better and evaluate faster you can use Optimizer which can find and evaluate
constant expressions and collapse expressions using rules of distributivity
