sudo apt-get install icdiff >/dev/null

cd -- "$( dirname -- "${BASH_SOURCE[0]}" )"

cd tests
    mv parse/*.scm scm 2>/dev/null
cd ..

if [ "$1" != "-java" ] && [ "$1" != "-python" ]
then
    printf "Arg error: 1st arg must be -java or -python\n"
    exit 1
fi

args=("$@")
files=${args[@]:1}

if [ -z "$files" ]; then
        files=`find tests/scm -type f -iname "*.scm" -printf '%f\n' | sed 's/\.scm$//1'`
    fi

if [ "$1" = "-java" ]
then
    cd java
        printf "Compiling Java..."
        ./gradlew build &> /dev/null &
        wait
        printf "Done\n\n"
    cd ..
fi

cd tests
    mkdir -p scm
    mkdir -p scan    
    rm -rf diff
    mkdir -p diff
    cd scm
        tests=0
        passed=0
        for file_ex in $files; 
        do
            file="${file_ex/.scm/""}" 
            ((++tests))
            printf "=> "
            ../../cxxscheme.exe -scan "$file.scm" > "../scan/$file.scan" &
            wait
            ../../cxxscheme.exe -parse "../scan/$file.scan" > "../parse/$file.parse" &
            wait
            sed -i '$d' "../parse/$file.parse"
            if [ "$1" = "-java" ]
            then
                java -jar ../../java/app/build/libs/app.jar -parse "../scan/$file.scan" > "../diff/$file.parse" &
            fi
            if [ "$1" = "-python" ]
            then
                python3 ../../python/slang.py -parse "../scan/$file.scan" > "../diff/$file.parse" &
            fi
            wait
            DIFF=$(diff -E -w -B -a --strip-trailing-cr "../parse/$file.parse" "../diff/$file.parse")
            if [ "$DIFF" != "" ] 
            then
                echo "FAILED: $file"
                icdiff --strip-trailing-cr --line-numbers  --color-map='add:red_bold,subtract:green_bold' --label="Solution (generated using cxxscheme)" --label="Your result (p3)" --cols=100 "../parse/$file.parse" "../diff/$file.parse"
            else 
                ((++passsed))
                echo "PASSED: $file"
            fi
            printf "\n"
        done
        printf "Tests passed: $passed/$tests\n"
    cd ..
cd ../