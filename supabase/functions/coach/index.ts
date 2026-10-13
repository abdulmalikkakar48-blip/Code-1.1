/** Runtime entry point; admission/authentication orchestration is independently testable. */
import { createClient } from "npm:@supabase/supabase-js@2";
import { createCoachHandler } from "./handler.ts";

Deno.serve(createCoachHandler({
  readEnv: name => Deno.env.get(name),
  createClient: (url, key, options) => createClient(url, key, options),
  fetch: globalThis.fetch,
  // Operational codes only: never log inputs, identity, keys or provider error bodies.
  warn: code => console.warn(code),
}));
