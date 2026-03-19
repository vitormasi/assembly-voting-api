import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Rate } from 'k6/metrics';

const erros = new Counter('erros');
const taxaSucesso = new Rate('taxa_sucesso');

export let options = {
    stages: [
        { duration: '10s', target: 50 },
        { duration: '30s', target: 500 },
        { duration: '30s', target: 1000 },
        { duration: '10s', target: 0 },
    ],
    thresholds: {
        http_req_duration: ['p(95)<2000'],
        taxa_sucesso: ['rate>0.95'],
    },
};

const BASE_URL = 'http://localhost:8080';

export function setup() {
    // 1. Cria a agenda
    const agendaPayload = JSON.stringify({
        title: 'Pauta Performance Test',
        description: 'Pauta criada automaticamente para teste de performance',
    });

    const agendaRes = http.post(`${BASE_URL}/agenda`, agendaPayload, {
        headers: { 'Content-Type': 'application/json' },
    });

    check(agendaRes, { '[SETUP] agenda criada': (r) => r.status === 201 });

    const agendaId = JSON.parse(agendaRes.body).id;
    console.log(`[SETUP] Agenda criada com ID: ${agendaId}`);

    // 2. Abre a agenda para votação (início agora, fim em 10 minutos)
    const now = new Date();
    const end = new Date(now.getTime() + 10 * 60 * 1000);

    const format = (date) => {
        const pad = (n) => String(n).padStart(2, '0');
        return `${pad(date.getDate())}/${pad(date.getMonth() + 1)}/${date.getFullYear()} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
    };

    const openRes = http.patch(
        `${BASE_URL}/agenda/${agendaId}/open?startAt=${encodeURIComponent(format(now))}&endAt=${encodeURIComponent(format(end))}`,
        null,
        { headers: { 'Content-Type': 'application/json' } }
    );

    check(openRes, { '[SETUP] agenda aberta': (r) => r.status === 200 });
    console.log(`[SETUP] Agenda ${agendaId} aberta para votação`);

    return { agendaId };
}

export default function (data) {
    const { agendaId } = data;

    // Gera CPF único por usuário e iteração (11 dígitos)
    const cpf = `${String(__VU).padStart(5, '0')}${String(__ITER).padStart(6, '0')}`;

    // POST /vote/agenda/{agendaId}?cpf=...&voteEnum=...
    const voteRes = http.post(
        `${BASE_URL}/vote/agenda/${agendaId}?cpf=${cpf}&voteEnum=SIM`,
        null,
        { tags: { name: 'registrar_voto' } }
    );

    const votoSucesso = check(voteRes, {
        '[VOTO] status é 201': (r) => r.status === 201,
        '[VOTO] tempo de resposta < 2s': (r) => r.timings.duration < 2000,
    });

    if (!votoSucesso) erros.add(1);
    taxaSucesso.add(votoSucesso);

    sleep(0.5);

    // GET /agenda/{id}/result
    const resultRes = http.get(
        `${BASE_URL}/agenda/${agendaId}/result`,
        { tags: { name: 'resultado_votacao' } }
    );

    const resultadoSucesso = check(resultRes, {
        '[RESULTADO] status é 200': (r) => r.status === 200,
        '[RESULTADO] tempo de resposta < 2s': (r) => r.timings.duration < 2000,
        '[RESULTADO] body não está vazio': (r) => r.body && r.body.length > 0,
    });

    if (!resultadoSucesso) erros.add(1);
    taxaSucesso.add(resultadoSucesso);

    if (__ITER === 0 && __VU === 1) {
        console.log(`[VOTO] Status: ${voteRes.status} | Body: ${voteRes.body}`);
        console.log(`[RESULTADO] Status: ${resultRes.status} | Body: ${resultRes.body}`);
    }

    sleep(1);
}
