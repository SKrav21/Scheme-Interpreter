SRC=./xmlFiles/
CXX=./cxxscheme.exe -parse
PYT=./python/slang.py -parse

for file in $SRC
do
   diff -q <($CXX $file) <($PYT $file)
   if [[ $? == "0" ]]
   then
      echo "correct output for $file"
   else
      echo "Incorrect output for $file"
fi
done
