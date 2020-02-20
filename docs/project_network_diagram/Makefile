FORMAT=-Tpng
DOT_FILES:=$(wildcard *.dot)

all: project_network_diagram

project_network_diagram: project_network_diagram.dot
	dot $(FORMAT) project_network_diagram.dot -o project_network_diagram.png

clean:
	rm *.png
