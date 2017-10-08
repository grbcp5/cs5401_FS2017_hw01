default: quickRun

quickRun:
	./run.sh config/EAQuickRun.xml config/50Shapes.txt

submit_1b_50:
	./run.sh config/submitRun config/50Shapes.txt

submit_1b_100:
	./run.sh config/submitRun config/100Shapes.txt

submit_1b_100Complex:
	./run.sh config/submitRun config/100ShapesComplex.txt

penalty50:
	./run.sh config/penalty50.xml config/50Shapes.txt

penalty100:
	./run.sh config/penalty100.xml config/100Shapes.txt

penalyt100Complex:
	./run.sh config/penalyt100Complex.xml config/100ShapesComplex.txt

selfAdaptive50:
	./run.sh config/selfAdaptive50.xml config/50Shapes.txt

selfAdaptive100:
	./run.sh config/selfAdaptive100.xml config/100Shapes.txt

selfAdaptive100Complex:
	./run.sh config/selfAdaptive100Complex.xml config/100ShapesComplex.txt

