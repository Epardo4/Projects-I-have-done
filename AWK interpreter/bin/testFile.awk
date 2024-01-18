BEGIN{
	FS = ",";
}
{
printf("$0 = %s          $1 = %s
", $0, $1)
}