Thư mục Dic: Chứa bộ Dictionary

Thư mục LM: Language model: 
	tuvung.txt ---->  text2wfreq < tuvung.txt | wfreq2vocab > tuvung.vocab ---->  tuvung.vocab 
	tuvung.vocab ---->   text2idngram -vocab tuvung.vocab -idngram tuvung.idngram < tuvung.txt ---->  tuvung.idngram
	tuvung.idngram ---->  idngram2lm -vocab_type 0 -idngram tuvung.idngram -vocab tuvung.vocab –arpa tuvung.arpa ----> tuvung.arpa
	tuvung.arpa ----> sphinx_lm_convert -i tuvung.arpa -o tuvung.lm.DMP ----> tuvung.lm.DMP

Thư mục etc: 
	Gồm Dictionary ( .dic, .phone ) + LM ( lm.DMP ) dùng để train, các file .transcription và .fileids khai báo các tệp âm thanh + nội dung âm thanh đó
	Tệp .filler là khoảng lặng giữa các từ
	Tệp feat.params + sphinx_train.cfg được generate sau bước setup: 'sphinxtrain -t tuvung setup'

Thư mục wav gồm các file ghi âm theo các từ có sẵn