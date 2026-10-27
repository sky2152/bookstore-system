import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "@/pages/Home";
import ScenarioSimulation from "@/pages/ScenarioSimulation";
import CareerPathGeneration from "@/pages/CareerPathGeneration"; // Added this line

export default function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/simulate" element={<ScenarioSimulation />} />
        <Route path="/career-path" element={<CareerPathGeneration />} /> {/* Added this line */}
        <Route path="/other" element={<div className="text-center text-xl">Other Page - Coming Soon</div>} />
      </Routes>
    </Router>
  );
}
