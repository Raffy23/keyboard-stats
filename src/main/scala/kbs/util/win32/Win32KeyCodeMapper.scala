package kbs.util.win32

import javafx.scene.input.KeyCode

/**
  * Created by: 
  *
  * @author Raphael
  * @version 11.07.2018
  */
object Win32KeyCodeMapper {
  import WindowsVKKeyCodes._

  def map(keyCode: Int): KeyCode = keyCode match {
    case VK_TAB => KeyCode.TAB
    case VK_CLEAR => KeyCode.CLEAR
    case VK_RETURN => KeyCode.ENTER

    case VK_SHIFT => KeyCode.SHIFT
    case VK_CONTROL => KeyCode.CONTROL
    case VK_MENU => KeyCode.CONTEXT_MENU
    case VK_PAUSE => KeyCode.PAUSE
    case VK_CAPITAL => KeyCode.CAPS
    case VK_KANA => KeyCode.KANA

    case VK_ESCAPE => KeyCode.ESCAPE
    case VK_CONVERT => KeyCode.CONVERT
    case VK_NONCONVERT => KeyCode.NONCONVERT
    case VK_ACCEPT => KeyCode.ACCEPT
    case VK_MODECHANGE => KeyCode.MODECHANGE

    case VK_SPACE => KeyCode.SPACE
    case VK_NEXT => KeyCode.TRACK_NEXT
    case VK_END => KeyCode.END
    case VK_HOME => KeyCode.HOME
    case VK_LEFT => KeyCode.LEFT
    case VK_UP => KeyCode.UP
    case VK_RIGHT => KeyCode.RIGHT
    case VK_DOWN => KeyCode.DOWN
    case VK_PRINT => KeyCode.PRINTSCREEN
    case VK_INSERT => KeyCode.INSERT
    case VK_DELETE => KeyCode.DELETE
    case VK_HELP => KeyCode.HELP

    case VK_KEY_0 => KeyCode.DIGIT0
    case VK_KEY_1 => KeyCode.DIGIT1
    case VK_KEY_2 => KeyCode.DIGIT2
    case VK_KEY_3 => KeyCode.DIGIT3
    case VK_KEY_4 => KeyCode.DIGIT4
    case VK_KEY_5 => KeyCode.DIGIT5
    case VK_KEY_6 => KeyCode.DIGIT6
    case VK_KEY_7 => KeyCode.DIGIT7
    case VK_KEY_8 => KeyCode.DIGIT8
    case VK_KEY_9 => KeyCode.DIGIT9

    case VK_KEY_A => KeyCode.A
    case VK_KEY_B => KeyCode.B
    case VK_KEY_C => KeyCode.C
    case VK_KEY_D => KeyCode.D
    case VK_KEY_E => KeyCode.E
    case VK_KEY_F => KeyCode.F
    case VK_KEY_G => KeyCode.G
    case VK_KEY_H => KeyCode.H
    case VK_KEY_I => KeyCode.I
    case VK_KEY_J => KeyCode.J
    case VK_KEY_K => KeyCode.K
    case VK_KEY_L => KeyCode.L
    case VK_KEY_M => KeyCode.M
    case VK_KEY_N => KeyCode.N
    case VK_KEY_O => KeyCode.O
    case VK_KEY_P => KeyCode.P
    case VK_KEY_Q => KeyCode.Q
    case VK_KEY_R => KeyCode.R
    case VK_KEY_S => KeyCode.S
    case VK_KEY_T => KeyCode.T
    case VK_KEY_U => KeyCode.U
    case VK_KEY_V => KeyCode.V
    case VK_KEY_W => KeyCode.W
    case VK_KEY_X => KeyCode.X
    case VK_KEY_Y => KeyCode.Y
    case VK_KEY_Z => KeyCode.Z

    case VK_LWIN => KeyCode.WINDOWS
    case VK_RWIN => KeyCode.WINDOWS

    case VK_NUMPAD0 => KeyCode.NUMPAD0
    case VK_NUMPAD1 => KeyCode.NUMPAD1
    case VK_NUMPAD2 => KeyCode.NUMPAD2
    case VK_NUMPAD3 => KeyCode.NUMPAD3
    case VK_NUMPAD4 => KeyCode.NUMPAD4
    case VK_NUMPAD5 => KeyCode.NUMPAD5
    case VK_NUMPAD6 => KeyCode.NUMPAD6
    case VK_NUMPAD7 => KeyCode.NUMPAD7
    case VK_NUMPAD8 => KeyCode.NUMPAD8
    case VK_NUMPAD9 => KeyCode.NUMPAD9

    case VK_MULTIPLY => KeyCode.MULTIPLY
    case VK_ADD => KeyCode.ADD
    case VK_SEPARATOR => KeyCode.SEPARATOR
    case VK_SUBTRACT => KeyCode.SUBTRACT
    case VK_DECIMAL => KeyCode.DECIMAL
    case VK_DIVIDE => KeyCode.DIVIDE

    case VK_F1 => KeyCode.F1
    case VK_F2 => KeyCode.F2
    case VK_F3 => KeyCode.F3
    case VK_F4 => KeyCode.F4
    case VK_F5 => KeyCode.F5
    case VK_F6 => KeyCode.F5
    case VK_F7 => KeyCode.F6
    case VK_F8 => KeyCode.F7
    case VK_F9 => KeyCode.F8
    case VK_F10 => KeyCode.F10
    case VK_F11 => KeyCode.F11
    case VK_F12 => KeyCode.F12
    case VK_F13 => KeyCode.F13
    case VK_F14 => KeyCode.F14
    case VK_F15 => KeyCode.F15
    case VK_F16 => KeyCode.F16
    case VK_F17 => KeyCode.F17
    case VK_F18 => KeyCode.F18
    case VK_F19 => KeyCode.F19
    case VK_F20 => KeyCode.F20
    case VK_F21 => KeyCode.F21
    case VK_F22 => KeyCode.F22
    case VK_F23 => KeyCode.F23
    case VK_F24 => KeyCode.F24

    case VK_NUMLOCK => KeyCode.NUM_LOCK
    case VK_SCROLL => KeyCode.SCROLL_LOCK
    case VK_LSHIFT => KeyCode.SHIFT
    case VK_RSHIFT => KeyCode.SHIFT
    case VK_LCONTROL => KeyCode.CONTROL
    case VK_RCONTROL => KeyCode.CONTROL
    case VK_LMENU => KeyCode.WINDOWS
    case VK_RMENU => KeyCode.WINDOWS
  }

  protected object WindowsVKKeyCodes {
    val VK_BACK = 0x08
    val VK_TAB = 0x09

    val VK_CLEAR = 0x0C
    val VK_RETURN = 0x0D

    val VK_SHIFT = 0x10
    val VK_CONTROL = 0x11
    val VK_MENU = 0x12
    val VK_PAUSE = 0x13
    val VK_CAPITAL = 0x14
    val VK_KANA = 0x15
    val VK_HANGUEL = 0x15
    val VK_HANGUL = 0x15

    val VK_ESCAPE = 0x1B
    val VK_CONVERT = 0x1C
    val VK_NONCONVERT = 0x1D
    val VK_ACCEPT = 0x1E
    val VK_MODECHANGE = 0x1F

    val VK_SPACE = 0x20
    val VK_PRIOR = 0x21
    val VK_NEXT = 0x22
    val VK_END = 0x23
    val VK_HOME = 0x24
    val VK_LEFT = 0x25
    val VK_UP = 0x26
    val VK_RIGHT = 0x27
    val VK_DOWN = 0x28
    val VK_SELECT = 0x29
    val VK_PRINT = 0x2A
    val VK_EXECUTE = 0x2B
    val VK_SNAPSHOT = 0x2C
    val VK_INSERT = 0x2D
    val VK_DELETE = 0x2E
    val VK_HELP = 0x2F

    val VK_KEY_0 = 0x30
    val VK_KEY_1 = 0x31
    val VK_KEY_2 = 0x32
    val VK_KEY_3 = 0x33
    val VK_KEY_4 = 0x34
    val VK_KEY_5 = 0x35
    val VK_KEY_6 = 0x36
    val VK_KEY_7 = 0x37
    val VK_KEY_8 = 0x38
    val VK_KEY_9 = 0x39

    val VK_KEY_A = 0x41
    val VK_KEY_B = 0x42
    val VK_KEY_C = 0x43
    val VK_KEY_D = 0x44
    val VK_KEY_E = 0x45
    val VK_KEY_F = 0x46
    val VK_KEY_G = 0x47
    val VK_KEY_H = 0x48
    val VK_KEY_I = 0x49
    val VK_KEY_J = 0x4A
    val VK_KEY_K = 0x4B
    val VK_KEY_L = 0x4C
    val VK_KEY_M = 0x4D
    val VK_KEY_N = 0x4E
    val VK_KEY_O = 0x4F
    val VK_KEY_P = 0x50
    val VK_KEY_Q = 0x51
    val VK_KEY_R = 0x52
    val VK_KEY_S = 0x53
    val VK_KEY_T = 0x54
    val VK_KEY_U = 0x55
    val VK_KEY_V = 0x56
    val VK_KEY_W = 0x57
    val VK_KEY_X = 0x58
    val VK_KEY_Y = 0x59
    val VK_KEY_Z = 0x5A

    val VK_LWIN = 0x5B
    val VK_RWIN = 0x5C
    val VK_APPS = 0x5D

    val VK_SLEEP = 0x5F

    val VK_NUMPAD0 = 0x60
    val VK_NUMPAD1 = 0x61
    val VK_NUMPAD2 = 0x62
    val VK_NUMPAD3 = 0x63
    val VK_NUMPAD4 = 0x64
    val VK_NUMPAD5 = 0x65
    val VK_NUMPAD6 = 0x66
    val VK_NUMPAD7 = 0x67
    val VK_NUMPAD8 = 0x68
    val VK_NUMPAD9 = 0x69

    val VK_MULTIPLY = 0x6A
    val VK_ADD = 0x6B
    val VK_SEPARATOR = 0x6C
    val VK_SUBTRACT = 0x6D
    val VK_DECIMAL = 0x6E
    val VK_DIVIDE = 0x6F

    val VK_F1 = 0x70
    val VK_F2 = 0x71
    val VK_F3 = 0x72
    val VK_F4 = 0x73
    val VK_F5 = 0x74
    val VK_F6 = 0x75
    val VK_F7 = 0x76
    val VK_F8 = 0x77
    val VK_F9 = 0x78
    val VK_F10 = 0x79
    val VK_F11 = 0x7A
    val VK_F12 = 0x7B
    val VK_F13 = 0x7C
    val VK_F14 = 0x7D
    val VK_F15 = 0x7E
    val VK_F16 = 0x7F
    val VK_F17 = 0x80
    val VK_F18 = 0x81
    val VK_F19 = 0x82
    val VK_F20 = 0x83
    val VK_F21 = 0x84
    val VK_F22 = 0x85
    val VK_F23 = 0x86
    val VK_F24 = 0x87

    val VK_NUMLOCK = 0x90
    val VK_SCROLL = 0x91

    val VK_LSHIFT = 0xA0
    val VK_RSHIFT = 0xA1
    val VK_LCONTROL = 0xA2
    val VK_RCONTROL = 0xA3
    val VK_LMENU = 0xA4
    val VK_RMENU = 0xA5
  }

}
