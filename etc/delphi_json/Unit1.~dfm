object Form1: TForm1
  Left = 192
  Top = 130
  Width = 979
  Height = 563
  Caption = 'Form1'
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -11
  Font.Name = 'MS Sans Serif'
  Font.Style = []
  OldCreateOrder = False
  OnCreate = FormCreate
  PixelsPerInch = 96
  TextHeight = 13
  object Button1: TButton
    Left = 432
    Top = 32
    Width = 75
    Height = 25
    Caption = 'Button1'
    TabOrder = 0
    OnClick = Button1Click
  end
  object Memo1: TMemo
    Left = 544
    Top = 32
    Width = 265
    Height = 153
    Lines.Strings = (
      '{'
      '  "classname":"41",'
      '  "pupils":'
      '    ['
      '        {'
      #9'"name":"sunway",'
      #9'"sex":"boy"'
      #9'},'
      '{"name":"bitcat","sex":"girl"}'
      ']'
      '}')
    TabOrder = 1
  end
  object Button2: TButton
    Left = 424
    Top = 88
    Width = 75
    Height = 25
    Caption = 'Button2'
    TabOrder = 2
  end
  object Memo2: TMemo
    Left = 8
    Top = 0
    Width = 337
    Height = 521
    Lines.Strings = (
      'Memo2')
    TabOrder = 3
  end
  object u: TIdUDPServer
    BufferSize = 65535
    BroadcastEnabled = True
    Bindings = <>
    DefaultPort = 20420
    OnUDPRead = uUDPRead
    ThreadedEvent = True
    Left = 384
    Top = 48
  end
  object Timer1: TTimer
    Interval = 3000
    OnTimer = Timer1Timer
    Left = 376
    Top = 144
  end
end
