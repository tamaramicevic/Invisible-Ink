FORMAT=-Tpng
DOT_FILES:=$(wildcard *.dot)

all: work_breakdown_structure sf4.1 sf4.2 sf4.3 sf4.4 sf4.5 sf4.6 sf4.7

work_breakdown_structure: work_breakdown_structure.dot
	dot $(FORMAT) work_breakdown_structure.dot -o work_breakdown_structure.png

sf4.1: sf4.1.dot
	dot $(FORMAT) sf4.1.dot -o sf4.1.png

sf4.2: sf4.2.dot
	dot $(FORMAT) sf4.2.dot -o sf4.2.png

sf4.3: sf4.3.dot
	dot $(FORMAT) sf4.3.dot -o sf4.3.png

sf4.4: sf4.4.dot
	dot $(FORMAT) sf4.4.dot -o sf4.4.png

sf4.5: sf4.5.dot
	dot $(FORMAT) sf4.5.dot -o sf4.5.png

sf4.6: sf4.6.dot
	dot $(FORMAT) sf4.6.dot -o sf4.6.png

sf4.7: sf4.7.dot
	dot $(FORMAT) sf4.7.dot -o sf4.7.png

clean:
	rm *.png
