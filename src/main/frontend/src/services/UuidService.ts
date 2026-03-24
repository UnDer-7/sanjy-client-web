import { v4 as uuidV4 } from 'uuid';

function randomUUID(): string {
  return uuidV4();
}

export const UuidService = {
  randomUUID,
};
