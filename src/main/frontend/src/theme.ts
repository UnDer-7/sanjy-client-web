import { createTheme, type MantineColorsTuple } from "@mantine/core";

// Custom color palette
const alabasterGrey: MantineColorsTuple = [
  '#f5f6f7',
  '#e9ebed',
  '#d9dde0',
  '#c7ced2',
  '#b7c0c6',
  '#adb7be',
  '#a8b3ba',
  '#939da5',
  '#848c94',
  '#727a81',
];

const thistle: MantineColorsTuple = [
  '#f9f5fc',
  '#efe8f5',
  '#dec9ee',
  '#cca8e7',
  '#bd8be1',
  '#b179dd',
  '#ab6fdc',
  '#965dc3',
  '#8652ae',
  '#754699',
];

const wisteria: MantineColorsTuple = [
  '#f7f4fb',
  '#e9e4f1',
  '#c7abdd',
  '#c7abdd',
  '#a971c9',
  '#9957bb',
  '#9047b4',
  '#7c389f',
  '#6f318f',
  '#60287e',
];

const dustyMauve: MantineColorsTuple = [
  '#f5f4f6',
  '#e6e5e8',
  '#cccad0',
  '#b1adb7',
  '#9a94a2',
  '#8d8296',
  '#867a90',
  '#73687d',
  '#675c70',
  '#594e63',
];

export const theme = createTheme({
  colors: {
    alabasterGrey,
    thistle,
    wisteria,
    dustyMauve,
  },
  primaryColor: 'wisteria',
  white: '#ffffff',
  black: '#000000',
});