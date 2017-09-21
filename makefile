default: quickRun

quickRun:
	./run.sh config/quickRun config/50Shapes.txt

submit50:
	./run.sh config/submitRun config/50Shapes.txt

submit100:
	./run.sh config/submitRun config/100Shapes.txt

submit100Complex:
	./run.sh config/submitRun config/100ShapesComplex.txt
