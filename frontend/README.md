# React + Vite

This template provides a minimal setup to get React working in Vite with HMR and some Oxlint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Oxc](https://oxc.rs)
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/)

## React Compiler

The React Compiler is not enabled on this template because of its impact on dev & build performances. To add it, see [this documentation](https://react.dev/learn/react-compiler/installation).

## Expanding the Oxlint configuration

If you are developing a production application, we recommend using TypeScript with type-aware lint rules enabled. Check out the [TS template](https://github.com/vitejs/vite/tree/main/packages/create-vite/template-react-ts) for information on how to integrate TypeScript and Oxlint's TypeScript related rules in your project.

## Doctor, laboratory and pharmacy workflow

Doctors see assigned patients on the home page and can open a patient profile to request lab tests or prescribe medication. Prescriptions require a medication already in the catalog and a department pharmacy; they start in `PENDING_PHARMACY_REVIEW`. Pharmacy staff select the pharmacy and estimate the cost using the medication's unit price and prescribed quantity. Estimation does not confirm payment or dispense stock.

Lab requests start with empty results. A lab scientist opens the request in Lab reports and submits a result for each requested test. Results appear on the patient profile and among the requesting doctor's recent lab results. Configure the doctor's linked profile, department pharmacies and medication prices beforehand.
