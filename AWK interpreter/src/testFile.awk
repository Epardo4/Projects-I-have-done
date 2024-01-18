function reverseLetters(toReverse){
length = length(toReverse);
toFormat = "";
for(i = length; i > 0; i--){
	toFormat = toFormat substr(toReverse, i, 1);
}
return toFormat;
}
BEGIN{
FS = ",";
}
{
printf("This is the value of the lines printed forwards: %s
", $0);
printf("This is the value of the lines printed backwards: %s
", reverseLetters($0));
}
($0 == $1){
printf("There are no commas in this line: %s
", $0);
}
{
i = 0;
while(i <= NF){
	if($i ~ `Hello` || $i ~ `Hi`)
		printf("%s contains a greeting phrase
", $i);
	else if($i ~ `Bye`)
		printf("%s contains a parting phrase
", $i);
	i++;
}
}
{
for(i = 1; i <= NF; i++)
	a[i] = $i;
fullLine = "";
for(i in a)
	fullLine = fullLine a[i] ",";
fullLine = substr(fullLine, 1, length(fullLine) - 1);
printf("This is the full line according to the algorithim:
%s
This is the full line according to the document:
%s
", fullLine, $0);
}
END{
a = 1; b = 2; c = 3; d = 4; e = 5; f = 6; g = 7; h = 8;
printf("%s
", h % f * b + a * c ^ d - g / e); 
}