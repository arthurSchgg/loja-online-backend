# Design System e Motion Design — proposta para validação visual

> **STATUS: PROVISÓRIO.** Não é a identidade oficial, não substitui logotipo, cores ou fotografias fornecidas pela proprietária. A implementação deve usar HTML/CSS/JS puro, conforme briefing.

## Direção visual
Delicadeza, sofisticação, acolhimento e sensação de cuidado. Fotografias de boa qualidade, espaçamento generoso, tipografia legível, sem aparência de template genérico.

## Paleta ilustrativa (proposta, não aprovada)
| Token | HEX | Uso |
|---|---|---|
| `--color-primary` | `#743F58` | identidade e botões principais |
| `--color-accent` | `#A56575` | detalhes e destaques |
| `--color-background` | `#FFF9F7` | fundo principal |
| `--color-surface` | `#FFFFFF` | cards e formulários |
| `--color-text` | `#2D2428` | texto principal |
| `--color-muted` | `#675B60` | texto secundário |
| `--color-border` | `#E6D9DD` | bordas |
| `--color-success` | `#256D45` | confirmação |
| `--color-warning` | `#885800` | avisos |
| `--color-error` | `#AB2337` | erro |

Verificar contraste real de cada combinação antes de implementar, especialmente texto sobre acentos.

```css
:root {
  --color-primary: #743F58;
  --color-accent: #A56575;
  --color-background: #FFF9F7;
  --color-surface: #FFFFFF;
  --color-text: #2D2428;
  --color-muted: #675B60;
  --color-border: #E6D9DD;
  --color-success: #256D45;
  --color-warning: #885800;
  --color-error: #AB2337;
  --space-1: .25rem; --space-2: .5rem; --space-3: .75rem;
  --space-4: 1rem; --space-6: 1.5rem; --space-8: 2rem;
  --radius-sm: .5rem; --radius-md: .875rem; --radius-lg: 1.5rem;
  --shadow-soft: 0 12px 32px rgb(45 36 40 / .08);
}
```

## Tipografia e estrutura
- Proposta: serifada editorial em títulos + sans-serif legível para interface; **famílias finais a validar**.
- Títulos com `clamp()`; body base 16px, line-height >=1.5; controle de densidade em mobile.
- Mobile first; breakpoints de projeto propostos: 640, 960 e 1280 px (ajustar por conteúdo, não apenas por dispositivo).
- Componentes: Header, Navbar, Hero, ProductCard, CategoryCard, BrandCard, Buttons, Forms, Select, Modal, Toast, Accordion, Breadcrumbs, Skeleton/Empty/Error/Loading, WhatsAppFab, Footer.
- Estados foco, hover, active, disabled, loading, error e teclado documentados.
- Frontend administrativo na mesma origem da API; jamais salvar credenciais ou CSRF em `localStorage`.

## Motion Design
| Elemento | Duração proposta | Easing | Gatilho |
|---|---|---|---|
| Reveal de seção | 320–480 ms | ease-out | scroll/entrada |
| Card hover | 150–220 ms | ease-out | mouse/foco |
| Menu mobile | 220–300 ms | ease-in-out | abrir/fechar |
| Passo de quiz | 250–350 ms | ease-in-out | avançar/voltar |
| Modal confirmação | 180–260 ms | ease-out | interação |
| Banner automático | pausa de 5 s | transição discreta | autoplay e controles |

- Preferir `transform` e `opacity`; evitar layout shift.
- Respeitar `prefers-reduced-motion: reduce` e permitir pausar carrossel.
- Animações não devem impedir leitura, foco, controle por teclado nem desempenho.
- IntersectionObserver pode reduzir custo de observação de scroll.

## Páginas
Home: hero, boas-vindas, chamada de experiências, destaques, categorias, marcas, novidades, institucional, velas e CTA WhatsApp. Depois: Conheça a Loja; Catálogo/detalhe; Experiências; Espaço das Velas; Contato/Política; Painel administrativo restrito.

## Verificações
Meta WCAG 2.2 AA; LCP <=2,5 s, INP <=200 ms, CLS <=0,1 (p75 onde mensurável). Testar com teclado, leitor de tela e dispositivos reais. Não anunciar certificação sem validação.
