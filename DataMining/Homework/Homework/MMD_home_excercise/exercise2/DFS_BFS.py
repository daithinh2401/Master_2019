class Graph:  
    def __init__(self, V): 
        self.V = V  
        self.graph = [[] for i in range(V)] 
      
    def addEdge(self, v, w): 
        self.graph[v].append(w)
      
    def DFSUtil(self, s, visited): 
          
        stack = [] 
        stack.append(s)  
      
        while (len(stack) != 0):  
            s = stack.pop() 
            if (not visited[s]): 
                print(s, end = " ")  
                visited[s] = True

            i = 0
            while i < len(self.graph[s]): 
                if (not visited[self.graph[s][i]]):  
                    stack.append(self.graph[s][i]) 
                i += 1

    def DFS(self): 
        visited = [False] * self.V 
        for i in range(self.V): 
            if (not visited[i]): 
                self.DFSUtil(i, visited) 
    
    def BFS(self): 

        s = 0
        visited = [False] * (len(self.graph))   
        queue = [] 
        queue.append(s)
        visited[s] = True
  
        while queue:
            s = queue.pop(0) 
            print (s, end = " ") 

            for i in self.graph[s]: 
                if visited[i] == False: 
                    queue.append(i) 
                    visited[i] = True
                    
if __name__ == '__main__': 
  
    g = Graph(5)

    g.addEdge(0, 1)
    g.addEdge(0, 2)
    g.addEdge(0, 4)

    g.addEdge(1, 0)  
    g.addEdge(1, 4)

    g.addEdge(2, 0)

    g.addEdge(3, 4)

    g.addEdge(4, 0)
    g.addEdge(4, 1)
    g.addEdge(4, 3)  
  
    print('\n===DFS===')
    g.DFS()

    print('\n===BFS===')
    g.BFS()

