unit Unit1;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs,json,StdCtrls, IdBaseComponent, IdComponent, IdUDPBase,
  IdUDPServer, ExtCtrls,idglobal,IdSocketHandle,encddecd;

type
  TForm1 = class(TForm)
    Button1: TButton;
    Memo1: TMemo;
    Button2: TButton;
    u: TIdUDPServer;
    Timer1: TTimer;
    Memo2: TMemo;
    procedure Button1Click(Sender: TObject);
    procedure FormCreate(Sender: TObject);
    procedure Timer1Timer(Sender: TObject);
    procedure uUDPRead(Sender: TObject; AData: TBytes;
      ABinding: TIdSocketHandle);
  private
    { Private declarations }
  public
    procedure l(s: string);
    function parserJson(s: string): string;
    { Public declarations }
  end;

var
  Form1: TForm1;
  c:integer;
  iRecvCount:integer;

implementation

{$R *.dfm}
function toBase64(s:string):string;
begin
  result:=EncodeString(s);
end;

function fromBase64(s:string):string;
begin
  result:=DecodeString(s);
end;

procedure TForm1.Button1Click(Sender: TObject);
var
  b,x,a:ISuperObject;
  i:integer;
  aa:TSuperArray;
begin
  a:=so();
  a.S['name']:='sunwaywei';
  a.S['sex']:='boy1';
  a.o['items']:=so('[]');
  for i:=1 to 10 do
    a.A['items'].Add(so('{"id":'+inttostr(i)+',"name":"IT'+inttostr(i)+'"}'));

  


  showmessage(a.AsJSon(true));

  x := SO(self.Memo1.Text);
  showmessage(x.S['classname']);
  aa:=x.O['pupils'].AsArray;
  if aa=nil then exit;
  for i:=0 to aa.Length-1 do
  begin
    showmessage(aa[i].S['name']);
  end;

end;

procedure TForm1.FormCreate(Sender: TObject);
begin
  self.u.DefaultPort:=20420;
  self.u.Active:=true;
  iRecvCount:=0;
end;

procedure TForm1.l(s: string);
begin
  if self.Memo2.Lines.Count>200 then self.memo2.clear;
  self.Memo2.Lines.Add(s);
end;

function TForm1.parserJson(s: string): string;
var
  b,x,a:ISuperObject;
  i:integer;
  aa:TSuperArray;
begin
  x:=so(s);
  aa:=x.AsArray;
  if aa=nil then exit;
  for i:=0 to aa.Length-1 do
  begin
    if i=0 then
    begin
      l('RecordCount='+aa[i].S['RecordCount']+',auth='+aa[i].S['Auth']);
    end
    else
    begin
      l('id='+aa[i].S['id']+',name='+aa[i].S['name']);
    end;
  end;
  
end;

procedure TForm1.Timer1Timer(Sender: TObject);
begin
  l('broadcast discover');
  u.Broadcast('tzjk_discover'#13#10,20423);
end;
function GetPart(StrSource,StrBegin,StrEnd:string):string;
var
i1,in_star,in_end:integer;
sSub:string;
y1,y2:integer;
begin
i1:=AnsiPos(strbegin,strsource);
if i1<1 then
begin
  result:='';
  exit;
end;
in_star:=i1+length(strbegin);
sSub:=copy(strsource,i1+length(strBegin),length(StrSource)-i1+1);
in_end:=AnsiPos(strend,sSub);

y1:=in_star;
y2:=in_end-length(strend);
result:=copy(sSub,0,in_end-1);
end;


function UTF8ToAnsiString(utf8str:string; CodePage: integer):AnsiString;
var
i:integer;
buffer:widestring;
ch,c1,c2:byte;
begin
result:='';
i:=1;
while i<=Length(utf8str) do
begin
ch:=byte(utf8str[i]);
setlength(buffer,length(buffer)+1);
if (ch and $80)=0 then //1-byte
buffer[length(buffer)]:=widechar(ch)
else begin
if (ch AND $E0) = $C0 then
begin // 2-byte
inc(i);
c1 := byte(utf8str[i]);
buffer[length(buffer)]:=widechar((word(ch AND $1F) SHL 6) OR (c1 AND $3F));
end
else
begin // 3-byte
inc(i);
c1 := byte(utf8str[i]);
inc(i);
c2 := byte(utf8str[i]);
buffer[length(buffer)]:=widechar((word(ch AND $0F) SHL 12) OR (word(c1 AND $3F) SHL 6) OR (c2 AND $3F));
end;
end;
inc(i);
end; //while
i := WideCharToMultiByte(codePage,WC_COMPOSITECHECK or WC_DISCARDNS or WC_SEPCHARS or WC_DEFAULTCHAR,@buffer[1], -1, nil, 0, nil, nil);
if i>1 then
begin
SetLength(Result, i-1);
WideCharToMultiByte(codePage,WC_COMPOSITECHECK or WC_DISCARDNS or WC_SEPCHARS or WC_DEFAULTCHAR,@buffer[1], -1, @Result[1], i-1, nil, nil);
end;
end;

function UTF8ToWide(const US: ansistring): WideString;
var
  len: integer;
  ws: WideString;
begin
  Result:='';
  if (Length(US) = 0) then
    exit;
  len:=MultiByteToWideChar(CP_UTF8, 0, PChar(US), -1, nil, 0);
  SetLength(ws, len);
  MultiByteToWideChar(CP_UTF8, 0, PChar(US), -1, PWideChar(ws), len);
  Result:=ws;
end;

procedure TForm1.uUDPRead(Sender: TObject; AData: TBytes;
  ABinding: TIdSocketHandle);
var
  fromIP:string;
  fromPort:integer;
  dt:string;
  a,b:ISuperObject;
  i:integer;
  cnt:integer;
  jsonStr:string;
  ret:string;
  jsonDecodeBase64:string;
begin
  //
  a:=so('[]');
  b:=so();
  b.I['RecordCount']:=123;
  b.S['AuthKey']:='ß÷ß÷123';
  a.AsArray.Add(b);
  for i:=1 to 5 do
  begin
    b.Clear(true);
    b.I['id']:=i;
    b.S['Dept']:='IT'+inttostr(i)+'²¿ÃÅ';
    a.AsArray.Add(b);
  end;


  iRecvCount:=iRecvCount+1;
  fromIP:=aBinding.PeerIP;
  fromPort:=aBinding.PeerPort;
  dt:=bytestostring(adata);
  //dt:=UTF8ToAnsiString(dt,936);
  jsonStr:=getPart(dt,'<json>','</json>');
  jsonDecodeBase64:=fromBase64(jsonStr);
  jsonDecodeBase64:=utf8towide(jsonDecodeBase64);
  ret:=self.parserJson(jsonDecodeBase64);

  l(fromIP+':'+inttostr(fromPort)+'='+inttostr(length(adata))+'bytes ');
  u.Send(fromIP,20423,'++<json>'+toBase64(a.AsJSon())+'</json>');
  self.Button2.Caption:=inttostr(iRecvCount);
end;

end.
