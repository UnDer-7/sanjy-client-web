import { Container, Title, Text } from '@mantine/core';
import {useEffect} from "react";
import Env from "../Env.ts";
import { v4 as uuidv4 } from 'uuid';

export function DietPlanPage() {
    console.log('url: ' + import.meta.env.VITE_API_BASE_URL)
    useEffect(() => {
       fetch(`${Env.API_BASE_URL}/diet-plan`, {
           headers: {
               'X-Correlation-ID': uuidv4()
           }
       })
        .then(r => r.text())
        .then(b => console.log(b));
    });

  return (
    <Container size="lg" py="xl">
      <Title order={1}>Diet Plan</Title>
      <Text c="dimmed" mt="md">
        Diet plan page content will be implemented here
      </Text>
    </Container>
  );
}
