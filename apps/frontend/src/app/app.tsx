// import {Route, Routes, Link} from 'react-router-dom';
import { useState } from 'react';
import { toast } from "sonner"

import { Button } from "@shared/ui";

export function App() {
  const [logs, setLogs] = useState('');
  const [loading, setLoading] = useState(false);

  const createIntegration = async () => {
    setLoading(true);
    try {
      const response = await fetch('http://localhost:8080/api/integrations/timer', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: 'test-timer',
          message: 'Hello from UI!',
          period: 5000
        })
      });
      const data = await response.json();
      toast.success('Integration created: ' + data.name);
    } catch (error) {
      toast.error('Failed to create integration');
    } finally {
      setLoading(false);
    }
  };

  const getLogs = async () => {
    setLoading(true);
    try {
      const response = await fetch('http://localhost:8080/api/integrations/test-timer/logs');
      const logsText = await response.text();
      setLogs(logsText);
    } catch (error) {
      toast.error('Failed to get logs');
    } finally {
      setLoading(false);
    }
  };

  const deleteIntegration = async () => {
    try {
      await fetch('http://localhost:8080/api/integrations/test-timer', {
        method: 'DELETE'
      });
      toast.success('Integration deleted');
      setLogs('');
    } catch (error) {
      toast.error('Failed to delete integration');
    }
  };

  return (
    <>
      <div className="p-8 max-w-4xl mx-auto">
        <h1 className="text-2xl font-bold mb-4">Test Camel K Integration</h1>

        <div className="space-y-4 mb-8">
          <Button onClick={createIntegration} disabled={loading}>
            Create Timer Integration
          </Button>
          <Button onClick={getLogs} variant="secondary" disabled={loading}>
            Get Logs
          </Button>
          <Button onClick={deleteIntegration} variant="destructive">
            Delete Integration
          </Button>
        </div>

        {logs && (
          <div className="bg-gray-900 text-green-400 p-4 rounded font-mono text-sm overflow-auto max-h-96">
            <pre>{logs}</pre>
          </div>
        )}
      </div>
      <div className="flex items-center justify-center h-screen">
        <Button 
          variant="outline" 
          onClick={() =>
          toast("Frontend app for Camel Forge", {
            description: "Build using React SPA",
          }) }>I will be</Button>
    </div>
    </>
  );
    {/* <div>
      <div role="navigation">
        <ul>
          <li>
            <Link to="/">Home</Link>
          </li>
          <li>
            <Link to="/page-2">Page 2</Link>
          </li>
        </ul>
      </div>
      <Routes>
        <Route
          path="/"
          element={
            <div>
              This is the generated root route. <Link to="/page-2">Click here for page 2.</Link>
            </div>
          }
        />
        <Route
          path="/page-2"
          element={
            <div>
              <Link to="/">Click here to go back to root page.</Link>
            </div>
          }
        />
      </Routes>
    </div>*/}


}

export default App;
