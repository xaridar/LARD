SCRIPT_PATH=`realpath "$0"`
DIR=`dirname "$SCRIPT_PATH"`
if [[ "$1" == "" ]];
	then
		`java -jar "%DIR"\lscript-1.0.0.jar`
else
	`java -jar "%DIR"\lscript-1.0.0.jar $1`
fi