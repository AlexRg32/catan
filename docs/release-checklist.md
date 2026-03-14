# Release Checklist

## Gameplay Validation
- [ ] Core commands validated (`roll_dice`, robber flow, trade, maritime, end_turn).
- [ ] New build commands validated (`build_road`, `build_settlement`, `build_city`, `buy_dev_card`).
- [ ] Hidden information verified via player-scoped projection.
- [ ] Win condition and end-game modal validated.
- [ ] `state.board` and `state.legalActions` projections validated in real match.

## Reliability
- [ ] `npm run test` green.
- [ ] `npm run build` green.
- [ ] Snapshot + delta reconnect flow manually validated.
- [ ] 3D board interaction fallback verified (`VITE_ENABLE_3D_BOARD=false`).
- [ ] Web FPS sanity check performed on desktop + mobile viewport.

## Operations
- [ ] Prometheus metrics visible at `/actuator/prometheus`.
- [ ] Dashboard loads with command latency and error rates.
- [ ] Alert rules loaded in Prometheus and synthetic trigger tested.

## Deployment
- [ ] Web and server Docker images build from current commit.
- [ ] Kubernetes manifests validated with `kubectl apply --dry-run=client -f infra/k8s`.
- [ ] Rollback target image tag documented before deploy.
